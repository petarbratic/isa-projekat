import { Component, OnInit } from '@angular/core';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../video.model';

@Component({
  selector: 'app-video-list',
  templateUrl: './video-list.component.html'
})
export class VideoListComponent implements OnInit {

  videos: VideoPost[] = [];

  constructor(public videoService: VideoService) {}

  ngOnInit(): void {
    this.videoService.getAll().subscribe(v => this.videos = v);
  }
}
