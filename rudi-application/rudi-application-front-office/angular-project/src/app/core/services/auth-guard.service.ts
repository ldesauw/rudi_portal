import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthenticationService} from './authentication.service';
import {LogService} from './log.service';
import {catchError, map, switchMap} from 'rxjs/operators';
import {UserService} from './user.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate, CanActivateChild {

    // Gestion d'un loader durant l'authentification
    public loader: boolean = false;

    constructor(
        public readonly authenticationService: AuthenticationService,
        private readonly userService: UserService,
        private readonly router: Router,
        private readonly logService: LogService) {
    }

    /**
     * tentative d'authent auto en mode anonymous
     * @return Observable<boolean> un observable qui dit si on a réussit ou non
     */
    private authenticateAnonymousAutomatically(): Observable<boolean> {
        // je tente l'authent
        return this.authenticationService.authenticateAsAnonymous().pipe(
            // C'est ok : je passe
            switchMap(() => of(true)),
            // C'est ko : je me redirige vers la page unauthorized (erreur grave)
            catchError(error => {
                this.logService.error('Authentication error', error);
                return this.redirectToNotAuthorized();
            })
        );
    }

    /**
     * Redirection vers unauthorized (la route)
     * @private
     */
    private redirectToNotAuthorized(): Observable<boolean> {
        // Le routeur fais go unauthorized
        this.router.navigate(['/not-authorized']);
        // je renvoie faux car c'est pas bon
        return of(false);
    }

    /**
     * Accès aux urls de même niveau
     * Renvoie true si utilisateur a le droit et false pas le droit
     * @returns Observable<any>
     */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
        Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        // On regarde si on est authentifié
        if (this.authenticationService.isAuthenticatedWithToken()) {
            // je regarde si mes tokens sont encore bon : je fais get me
            return this.userService.getConnectedUser().pipe(
                // ça marche : je peux accéder normalement à la page en restant authentifié
                map(() => true),
                // KO : j'ai pas accès à la page, j'ai perdu mon authent donc j'essaye de m'authent en anonymous
                catchError(() => this.authenticateAnonymousAutomatically())
            );
        }
        // Je suis dans l'état : j'ai pas réussi à m'authent en anonymous
        else if (this.authenticationService.authenticationFailedForAnonymous()) {
            return this.redirectToNotAuthorized();
        }
        // Je suis pas authentifié (tout court) authent automatique en mode anonymous
        else {
            return this.authenticateAnonymousAutomatically();
        }
    }

    /**
     * Accès aux urls enfants même traitement que les url parents
     * @returns Observable<any>
     */
    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
        Observable<boolean | UrlTree> |
        Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.canActivate(route, state);
    }
}
