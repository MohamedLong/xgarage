import { TenantService } from 'src/app/xgarage/common/service/tenant.service';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { trigger, style, transition, animate, AnimationEvent } from '@angular/animations';
import { MegaMenuItem } from 'primeng/api';
import { AppComponent } from './app.component';
import { AppMainComponent } from './app.main.component';
import { AuthService } from './auth/services/auth.service';
import { Router } from '@angular/router';
import { Tenant } from "src/app/xgarage/common/model/tenant";
import { XgarageComponent } from './xgarage/xgarage.component';
@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html',
    animations: [
        trigger('topbarActionPanelAnimation', [
            transition(':enter', [
                style({ opacity: 0, transform: 'scaleY(0.8)' }),
                animate('.12s cubic-bezier(0, 0, 0.2, 1)', style({ opacity: 1, transform: '*' })),
            ]),
            transition(':leave', [
                animate('.1s linear', style({ opacity: 0 }))
            ])
        ])
    ]
})
export class AppTopBarComponent {

    constructor(public appMain: XgarageComponent, public app: AppComponent,
        private authService: AuthService, private router: Router, private tenantService: TenantService) {
    }

    activeItem: number;
    userId: number;
    firstName: string;
    lastName: string;
    tenant: Tenant;
    tenantName: string;
    tenantType: string;

    @ViewChild('searchInput') searchInputViewChild: ElementRef;


    ngOnInit(): void {
        if (this.authService.isLoggedIn()) {

            this.firstName = JSON.parse(this.authService.getStoredUser()).firstName;
            this.lastName = JSON.parse(this.authService.getStoredUser()).lastName;
            this.tenantName = JSON.parse(this.authService.getStoredUser()).tenant.name;
            this.tenantType = JSON.parse(this.authService.getStoredUser()).tenant.tenantType.name;
        }
    }

    onSearchAnimationEnd(event: AnimationEvent) {
        switch (event.toState) {
            case 'visible':
                this.searchInputViewChild.nativeElement.focus();
                break;
        }
    }

    viewProfile(id: number) {
        localStorage.removeItem('supplierId');
        this.userId = JSON.parse(this.authService.getStoredUser()).id;
        this.tenantService.selectedTenantId = JSON.parse(this.authService.getStoredUser()).tenant.id;
        this.router.navigate(['/supplier-profile']);
    }

    doLogout() {
        this.authService.doLogoutUser();
        this.router.navigate(['/login']);
    }
}
