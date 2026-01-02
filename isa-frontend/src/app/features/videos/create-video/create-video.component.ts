import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VideoService } from 'src/app/core/services/video.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-video',
  templateUrl: './create-video.component.html'
})
export class CreateVideoComponent {

  form: FormGroup;
  videoFile!: File;
  thumbnailFile!: File;
  submitting = false;
  videos: any[] = [];
  constructor(
    private fb: FormBuilder,
    public videoService: VideoService,
    private router: Router
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      tags: [''],
      location: ['']
    });
  }

  onVideoSelected(event: any) {
    this.videoFile = event.target.files[0];
  }

  onThumbnailSelected(event: any) {
    this.thumbnailFile = event.target.files[0];
  }

  submit() {
    if (!this.videoFile || !this.thumbnailFile) return;

    const data = {
      title: this.form.value.title,
      description: this.form.value.description,
      tags: this.form.value.tags
        ? this.form.value.tags.split(',').map((t: string) => t.trim())
        : [],
      location: this.form.value.location
    };

    const formData = new FormData();
    formData.append(
      'data',
      new Blob([JSON.stringify(data)], { type: 'application/json' })
    );
    formData.append('video', this.videoFile);
    formData.append('thumbnail', this.thumbnailFile);

    this.submitting = true;

    this.videoService.create(formData).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.submitting = false
    });
  }
}
