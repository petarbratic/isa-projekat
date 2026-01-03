import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../videos/video.model';

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

  comments: { author: string; text: string; createdAt: Date }[] = [
    { author: 'Marko', text: 'Odličan video!', createdAt: new Date('2026-01-01T12:00:00') },
    { author: 'Ana', text: 'Može li nastavak?', createdAt: new Date('2026-01-02T09:30:00') }
  ];

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

  addCommentMock(): void {
    if (!this.newComment.trim()) return;

    this.comments.unshift({
      author: 'You',
      text: this.newComment,
      createdAt: new Date()
    });

    this.newComment = '';
  }
}
