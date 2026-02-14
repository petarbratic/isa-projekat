import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../videos/video.model';
import { PageResponse, CommentResponse } from 'src/app/models/comment.model';
import { AuthService } from 'src/app/core/services/auth.service';
import Hls from 'hls.js';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit {

  playerMode: 'LOADING' | 'BLOCKED' | 'HLS' | 'MP4' | 'ERROR' = 'LOADING';

  private _playerRef?: ElementRef<HTMLVideoElement>;
  private pendingAttach: (() => void) | null = null;

  @ViewChild('player', { static: false })
  set playerRef(v: ElementRef<HTMLVideoElement> | undefined) {
    this._playerRef = v;
    if (v && this.pendingAttach) {
      const fn = this.pendingAttach;
      this.pendingAttach = null;
      fn();
    }
  }

  private get videoEl(): HTMLVideoElement | null {
    return this._playerRef?.nativeElement ?? null;
  }

  private hls: Hls | null = null;

  videoId!: number;
  video: VideoPost | null = null;

  newComment = '';

  comments: CommentResponse[] = [];
  page = 0;
  size = 10;
  totalPages = 0;

  loadingComments = false;
  commentError: string | null = null;

  maxCommentLength = 500;
  minCommentLength = 5;

  constructor(
    private route: ActivatedRoute,
    public videoService: VideoService,
    public authService: AuthService
  ) {}

  private requireLogin(message: string): boolean {
    if (this.authService.tokenIsPresent()) return true;
    this.commentError = message;
    return false;
  }

  ngOnInit(): void {
    this.videoId = Number(this.route.snapshot.paramMap.get('id'));

    this.videoService.getById(this.videoId).subscribe({
      next: (v) => this.video = v,
      error: (err) => console.error('Failed to load video:', err)
    });

    this.loadComments(0);

    this.loadPremiere();
  }

  toggleLike(): void {
    if (!this.video) return;
    if (!this.requireLogin('You must be logged in to like posts.')) return;

    const prevLiked = this.video.likedByMe;
    const prevCount = this.video.likesCount;

    // optimistic update
    this.video.likedByMe = !this.video.likedByMe;
    this.video.likesCount += this.video.likedByMe ? 1 : -1;

    this.videoService.toggleLike(this.video.id, this.video.likedByMe).subscribe({
      next: () => {},
      error: () => {
        // rollback
        this.video!.likedByMe = prevLiked;
        this.video!.likesCount = prevCount;
        this.commentError = 'Failed to like the video.';
      }
    });
  }

  loadComments(page: number): void {
    this.loadingComments = true;
    this.commentError = null;

    this.videoService.getComments(this.videoId, page, this.size).subscribe({
      next: (res) => {
        this.comments = res.content;
        this.page = res.number;
        this.totalPages = res.totalPages;
        this.loadingComments = false;
      },
      error: (err) => {
        this.commentError = 'Failed to load comments.';
        this.loadingComments = false;
        console.error(err);
      }
    });
  }

  addComment(): void {
    if (!this.requireLogin('You must be logged in to comment posts.')) return;

    const text = this.newComment?.trim() ?? '';

    if (!text) {
      this.commentError = 'Comment text is required.';
      return;
    }

    if (text.length < this.minCommentLength) {
      this.commentError = `Comment must be at least ${this.minCommentLength} characters.`;
      return;
    }

    if (text.length > this.maxCommentLength) {
      this.commentError = `Comment must be at most ${this.maxCommentLength} characters.`;
      return;
    }

    this.commentError = null;


    this.videoService.addComment(this.videoId, this.newComment.trim()).subscribe({
      next: () => {
        this.newComment = '';
        this.loadComments(0); // najnoviji komentar je na vrhu
      },
      error: (err) => {
        if (err?.status === 429) {
          this.commentError = 'Too many comments! The limit is 60 comments per hour.';
        } else if (err?.status === 401) {
          this.commentError = 'You must be logged in to post a comment.';
        } else {
          this.commentError = 'Failed to add comment.';
        }
        console.error(err);
      }
    });
  }

  nextPage(): void {
    if (this.page + 1 >= this.totalPages) return;
    this.loadComments(this.page + 1);
  }

  prevPage(): void {
    if (this.page === 0) return;
    this.loadComments(this.page - 1);
  }

  private loadPremiere(): void {
    this.playerMode = 'LOADING';

    this.videoService.getPremiere(this.videoId).subscribe({
      next: (p) => {

        if (!p.available) {
          this.playerMode = 'BLOCKED';
          return;
        }

        if (p.mode === 'MP4') {
          if (!p.url) {
            this.playerMode = 'ERROR';
            return;
          }

          this.playerMode = 'MP4';
          const url = this.absoluteBackendUrl(p.url!);
          this.pendingAttach = () => this.attachMp4(url);
          return;
        }

        if (p.mode === 'HLS') {
          if (!p.url) {
            this.playerMode = 'ERROR';
            return;
          }

          this.playerMode = 'HLS';
          const url = this.absoluteBackendUrl(p.url!);
          const off = p.offsetSeconds || 0;
          this.pendingAttach = () => this.attachHls(url, off);
          return;
        }

        this.playerMode = 'ERROR';
      },
      error: () => {
        this.playerMode = 'ERROR';
      }
    });
  }
  private attachMp4(url: string): void {
    const el = this.videoEl;
    console.log('[MP4] attach url=', url, 'el=', el);

    if (!el) {
      console.warn('[MP4] playerRef is undefined (video element not rendered yet)');
      return;
    }

    el.onloadedmetadata = () => console.log('[MP4] loadedmetadata duration=', el.duration);
    el.onerror = () => console.log('[MP4] error', el.error);

    el.pause();
    el.src = url;
    el.load();

    el.play().then(() => console.log('[MP4] playing'))
      .catch(e => console.log('[MP4] play() rejected:', e));
  }

  private attachHls(url: string, offsetSeconds: number): void {
    const el = this.videoEl;
    if (!el) return;

    const canNative = el.canPlayType('application/vnd.apple.mpegurl') !== '';

    if (canNative) {
      el.src = url;
      el.addEventListener('loadedmetadata', () => {
        el.currentTime = offsetSeconds;
        el.play().catch(() => {});
      }, { once: true });
      return;
    }

    if (!Hls.isSupported()) {
      this.playerMode = 'ERROR';
      return;
    }

    this.hls = new Hls();
    this.hls.loadSource(url);
    this.hls.attachMedia(el);

    this.hls.on(Hls.Events.MANIFEST_PARSED, () => {
      el.addEventListener('loadedmetadata', () => {
        el.currentTime = offsetSeconds;
        el.play().catch(() => {});
      }, { once: true });
    });
  }
  
  private absoluteBackendUrl(u: string): string {
    if (u.startsWith('/')) return `http://localhost:8081${u}`;
    return u;
  }

}
