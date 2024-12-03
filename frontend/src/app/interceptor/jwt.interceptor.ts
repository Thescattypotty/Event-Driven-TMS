import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
	if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
		const token = localStorage.getItem('accessToken');

		if (token) {
			const request = req.clone({
				setHeaders: {
					Authorization: `Bearer ${token}`
				}
			});
			return next(request);
		}
	}
	return next(req);
};
