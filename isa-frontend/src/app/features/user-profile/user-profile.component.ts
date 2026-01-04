import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from 'src/app/core/services/user.service';
import { VideoService } from 'src/app/core/services/video.service';
import { VideoPost } from '../videos/video.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  user: any = null;
  videos: VideoPost[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    public userService: UserService,
    public videoService: VideoService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProfile(id);
  }

  private loadProfile(userId: number): void {
    this.loading = true;
    this.error = null;

    this.userService.getPublicProfile(userId).subscribe({
      next: (u) => {
        this.user = u;

        this.videoService.getByUser(userId).subscribe({
          next: (v) => {
            this.videos = v;
            this.loading = false;
          },
          error: () => {
            this.videos = [];
            this.loading = false;
          }
        });
      },
      error: () => {
        this.error = 'Profil nije pronađen.';
        this.loading = false;
      }
    });
  }
}