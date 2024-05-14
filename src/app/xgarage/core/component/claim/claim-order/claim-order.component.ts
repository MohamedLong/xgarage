import { Component, OnInit } from '@angular/core';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { ClaimService } from '../../../service/claim.service';
import { GenericComponent } from 'src/app/xgarage/common/generic/genericcomponent';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DatePipe } from '@angular/common';
import { AuthService } from 'src/app/auth/services/auth.service';

@Component({
    selector: 'app-claim-order',
    templateUrl: './claim-order.component.html',
    styles: [''],
    providers: [MessageService, ConfirmationService, DatePipe]
})
export class ClaimOrderComponent extends GenericComponent implements OnInit {

    constructor(private authService: AuthService, public datePipe: DatePipe, public route: ActivatedRoute, private router: Router, public breadcrumbService: AppBreadcrumbService, private claimService: ClaimService) {
        super(route, datePipe, breadcrumbService);
    }

    totalOrderAmount = 0;
    totalAmount = 0;
    delivaryTotal = 0;
    roleName: string = JSON.parse(this.authService.getStoredUser()).roles[0].roleName;
    role: number;
    garageTenant = 2;

    ngOnInit(): void {
        this.resetRouterLink();

        if (sessionStorage.getItem('claim-order')) {
            sessionStorage.removeItem('claim-order')
        }


        this.getClaimOrders();
        this.breadcrumbService.setItems([{ 'label': 'Orders', routerLink: ['orders'] }]);
    }

    getClaimOrders() {
        this.claimService.getClaimOrders().subscribe(res => {
            console.log(res)
            res.forEach(data => {
                this.totalOrderAmount = this.totalOrderAmount + data.orderAmount;
                this.totalAmount = this.totalAmount + data.totalAmount;
                this.delivaryTotal = this.delivaryTotal + data.deliveryFees;
            })

            res.reverse();
            this.masterDtos = res;
            this.cols = [
                { field: 'id', header: 'ID' },
                { field: 'createdAt', header: 'Date' },
                { field: 'customerName', header: 'Customer Name' },
                { field: 'supplierName', header: 'Supplier Name' },
                { field: 'claimNumber', header: 'Claim Number' },
                { field: 'orderAmount', header: 'Order Amount' },
                { field: 'totalAmount', header: 'Total Amount' },
                { field: 'orderStatus', header: 'Order Status' },
                { field: 'deliveryFees', header: 'Delivery Fees' }
            ];
        })
    }

    goOrderDetails(order: any) {
        sessionStorage.setItem('claim-order', JSON.stringify(order));
        this.updateRouterLink();
        this.router.navigate(['claim-order-details'], { queryParams: {} });
    }

    updateRouterLink() {
        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'claim-orders';
        });

        if (page) {
            console.log('updating router>')
            page.subMenu.routerLink = 'claim-order-details';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

    resetRouterLink() {
        console.log('resetting router>')
        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'claim-order-details';
        });

        if (page) {
            page.subMenu.routerLink = 'claim-orders';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

    insruanceOrGarage() {
        var tenant = JSON.parse(this.authService.getStoredUser()).tenant.tenantType.id;
        return (tenant = this.garageTenant);
    }
}
