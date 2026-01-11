import { Component, OnInit } from '@angular/core';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../video.model';

@Component({
  selector: 'app-video-list',
  templateUrl: './video-list.component.html',
  styleUrls: ['./video-list.component.scss']
})
export class VideoListComponent implements OnInit {

  videos: VideoPost[] = [];
  playingVideoId: number | null = null;

  constructor(public videoService: VideoService) {}

  ngOnInit(): void {
    this.videoService.getAll().subscribe(v => this.videos = v);
  }

  playVideo(videoId: number) {
    this.playingVideoId = videoId;
  }
}

