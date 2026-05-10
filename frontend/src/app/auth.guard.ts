// import {inject} from '@angular/core';
// import {CanActivateFn, Router} from '@angular/router';
// import {filter, take} from "rxjs";
// import {map} from "rxjs/operators";
// import {toObservable} from "@angular/core/rxjs-interop";
// import {AuthService} from "./core/services/auth.service";
//
//
// export const authGuard: CanActivateFn = () => {
//     const authService = inject(AuthService);
//     const router = inject(Router);
//     return toObservable(authService.user).pipe(
//         filter((user) => user !== undefined),
//         take(1),
//         map((user) => {
//             if (user) {
//                 return true;
//             } else {
//                 return router.parseUrl('/');
//             }
//         })
//     );
// }