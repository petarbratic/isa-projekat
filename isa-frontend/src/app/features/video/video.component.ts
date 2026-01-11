import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../videos/video.model';
import { PageResponse, CommentResponse } from 'src/app/models/comment.model';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit {

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
  }

  toggleLike(): void {
    if (!this.video) return;
    if (!this.requireLogin('You must be logged in to like posts.')) return;

    const prevLiked = this.video.isLikedByMe;
    const prevCount = this.video.likesCount;

    // optimistic update
    this.video.isLikedByMe = !this.video.isLikedByMe;
    this.video.likesCount += this.video.isLikedByMe ? 1 : -1;

    this.videoService.toggleLike(this.video.id, this.video.isLikedByMe).subscribe({
      next: () => {},
      error: () => {
        // rollback
        this.video!.isLikedByMe = prevLiked;
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
}
