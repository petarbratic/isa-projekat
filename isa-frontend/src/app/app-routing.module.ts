import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './features/home/home.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { LoginComponent } from './features/auth/login/login.component';
import { ActivateComponent } from './features/auth/activate/activate.component';

import { CreateVideoComponent } from './features/videos/create-video/create-video.component';
import { VideoComponent } from './features/video/video.component';
import { UserProfileComponent } from './features/user-profile/user-profile.component';

import { ShellComponent } from './layout/shell/shell.component';

const routes: Routes = [
  // STRANICE BEZ HEADERA
  // (trenutno nema nijedne)

  // STRANICE SA HEADEROM
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'signup', component: SignupComponent },
      { path: 'activate', component: ActivateComponent },
      { path: '', component: HomeComponent },          // HOME
      { path: 'upload', component: CreateVideoComponent },
      { path: 'profile/:id', component: UserProfileComponent },
      { path: 'video/:id', component: VideoComponent }
    ]
  },

  // fallback
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
