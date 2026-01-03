import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { LoginComponent } from './features/auth/login/login.component';

import { CreateVideoComponent } from './features/videos/create-video/create-video.component';
import { VideoListComponent } from './features/videos/video-list/video-list.component';

import { ActivateComponent } from './features/auth/activate/activate.component';


const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'signup', component: SignupComponent },

  { path: 'login', component: LoginComponent},
  { path: 'upload', component: CreateVideoComponent },
{ path: '', component: VideoListComponent }

  { path: 'login', component: LoginComponent },
  { path: 'activate', component: ActivateComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
