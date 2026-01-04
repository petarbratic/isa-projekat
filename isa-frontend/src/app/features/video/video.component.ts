import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../videos/video.model';
import { PageResponse, CommentResponse } from 'src/app/models/comment.model';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit {

  videoId!: number;
  video: VideoPost | null = null;

  likes = 0;
  dislikes = 0;
  myReaction: 'like' | 'dislike' | null = null;

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
    public videoService: VideoService
  ) {}

  ngOnInit(): void {
    this.videoId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadReactionsMock();

    this.videoService.getById(this.videoId).subscribe({
      next: (v) => this.video = v,
      error: (err) => console.error('Failed to load video:', err)
    });

    this.loadComments(0);
  }

  private loadReactionsMock(): void {
    this.likes = 12;
    this.dislikes = 3;
    this.myReaction = null;
  }

  like(): void {
    if (this.myReaction === 'like') {
      this.myReaction = null;
      this.likes--;
      return;
    }

    if (this.myReaction === 'dislike') {
      this.dislikes--;
    }

    this.myReaction = 'like';
    this.likes++;
  }

  dislike(): void {
    if (this.myReaction === 'dislike') {
      this.myReaction = null;
      this.dislikes--;
      return;
    }

    if (this.myReaction === 'like') {
      this.likes--;
    }

    this.myReaction = 'dislike';
    this.dislikes++;
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
