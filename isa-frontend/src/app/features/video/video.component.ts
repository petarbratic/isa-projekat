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

  constructor(
    private route: ActivatedRoute,
    public videoService: VideoService
  ) {}

  ngOnInit(): void {
    this.videoId = Number(this.route.snapshot.paramMap.get('id'));

    this.videoService.getById(this.videoId).subscribe({
      next: (v) => this.video = v,
      error: (err) => console.error('Failed to load video:', err)
    });
  }
}