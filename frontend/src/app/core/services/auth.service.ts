import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from "../models/tracking.models";

@Injectable({providedIn: 'root'})
export class AuthService {
    private http = inject(HttpClient);
    private base = '/api';

    private _user = signal<User | null | undefined>(undefined);
    readonly user = this._user.asReadonly();

    checked = signal(false);

    constructor() {
        this.initializeAuth();
    }

    private initializeAuth() {
        const savedUser = localStorage.getItem('user_session');
        if (savedUser) {
            this._user.set(JSON.parse(savedUser));
        } else {
            this._user.set(null);
        }
        console.warn(savedUser);
    }

    async loadUser() {
        await this.http.get<User>(`${this.base}/me`).subscribe({
            next: (user) => {
                localStorage.setItem('user_session', JSON.stringify(user));
                this._user.set(user);
                this.checked.set(true);
            },
            error: (err) => {
                console.warn(err);
                this._user.set(null);
                this.checked.set(true);
            }
        });
    }

    signIn() {
        window.location.href = '/oauth2/authorization/google';
        // window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    }

    signOut() {
        window.location.href = '/api/signout';
        localStorage.removeItem('user_session');
        this._user.set(null);
    }
}
