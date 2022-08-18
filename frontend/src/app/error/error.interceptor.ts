import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthorizedUserService } from '../user/authorized-user.service';


@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private userService: AuthorizedUserService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError(
        (error: HttpErrorResponse) => {
          this.testAuthorization(error);
          return throwError(error);
        }
      )
    );
  }

  private testAuthorization(error: HttpErrorResponse): void {
    if (error.status === 401) {
      this.userService.clearData();
    }
  }
}
