import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VideoService } from 'src/app/core/services/video.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-video',
  templateUrl: './create-video.component.html',
  styleUrls: ['./create-video.component.scss']
})
export class CreateVideoComponent {

  form: FormGroup;
  videoFile!: File;
  thumbnailFile!: File;
  submitting = false;
  videos: any[] = [];
  locations = ['Novi Sad', 'Beograd', 'Niš', 'Kragujevac'];
  errorMessage: string = '';
  constructor(
    private fb: FormBuilder,
    public videoService: VideoService,
    private router: Router
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      tags: [''],
      location: ['Novi Sad', Validators.required]
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
    this.errorMessage = '';
    
    this.videoService.create(formData).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {
        this.submitting = false;

        if (err?.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Video ima više od 200MB. Neuspešan upload videa. Pokušajte ponovo.';
        }
      }
    });
  }
}
