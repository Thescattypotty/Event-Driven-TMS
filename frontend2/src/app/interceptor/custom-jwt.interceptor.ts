import { HttpInterceptorFn } from '@angular/common/http';

export const customJwtInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req);
};
