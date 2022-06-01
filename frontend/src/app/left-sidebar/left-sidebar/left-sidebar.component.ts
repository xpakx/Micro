import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faComment, faFire, faHeart, faNewspaper, faTag } from '@fortawesome/free-solid-svg-icons';
import { TagDetails } from 'src/app/tag/dto/tag-details';
import { TagService } from 'src/app/tag/tag.service';

@Component({
  selector: 'app-left-sidebar',
  templateUrl: './left-sidebar.component.html',
  styleUrls: ['./left-sidebar.component.css']
})
export class LeftSidebarComponent implements OnInit {
  faNew = faNewspaper;
  faHot = faFire;
  faFav = faHeart;
  faTag = faTag;
  faActive = faComment;
  tags: TagDetails[] = [];
  showTags: boolean = true;

  constructor(private tagService: TagService, private router: Router) { }

  ngOnInit(): void {
    this.tagService.getTopTags().subscribe({
      next: (response: TagDetails[]) => this.updateTags(response),
      error: (error: HttpErrorResponse) => this.showError(error)
    });
  }

  showError(error: HttpErrorResponse): void {
    this.showTags = false;
  }

  updateTags(response: TagDetails[]): void {
    this.tags = response;
  }

  toTag(tag: String) {
    this.router.navigate([`tag/${tag}`])
  }

  toMain() {
    this.router.navigate([''])
  }

  toHot() {
    this.router.navigate(['hot'])
  }

  toActive() {
    this.router.navigate(['active'])
  }

  toFav() {
    this.router.navigate(['fav'])
  }
}
