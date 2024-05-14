import { DatePipe } from '@angular/common';
import { Component, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { AuthService } from 'src/app/auth/services/auth.service';
import { DataService } from 'src/app/xgarage/common/generic/dataservice';
import { GenericComponent } from 'src/app/xgarage/common/generic/genericcomponent';
import { OrderService } from '../../service/order.service';

@Component({
    selector: 'order',
    templateUrl: './order.component.html',
    styleUrls: ['../../../../demo/view/tabledemo.scss'],
    styles: [`
        :host ::ng-deep .p-dialog .product-image {
            width: 150px;
            margin: 0 auto 2rem auto;
            display: block;
        }

        @media screen and (max-width: 960px) {
            :host ::ng-deep .p-datatable.p-datatable-customers .p-datatable-tbody > tr > td:last-child {
                text-align: center;
            }

            :host ::ng-deep .p-datatable.p-datatable-customers .p-datatable-tbody > tr > td:nth-child(6) {
                display: flex;
            }
        }

    `],
    providers: [MessageService, ConfirmationService, DatePipe]
})
export class OrderComponent extends GenericComponent implements OnInit {

    constructor(private authService: AuthService, private orderService: OrderService, public route: ActivatedRoute, private router: Router, private dataService: DataService<any>, public confirmationService: ConfirmationService,
        public messageService: MessageService, public datePipe: DatePipe, breadcrumbService: AppBreadcrumbService) {
            super(route, datePipe, breadcrumbService);
    }

    role: number;
    roleName: string = JSON.parse(this.authService.getStoredUser()).roles[0].roleName;
    pageNo: number = 0;
    totalOrderAmount = 0;
    totalAmount = 0;
    delivaryTotal = 0;
    garageTenant = 2;

    ngOnInit(): void {
        this.resetRouterLink();

        if(sessionStorage.getItem('order')) {
            sessionStorage.removeItem('order')
        }

        if (this.roleName == 'ROLE_SUPPLIER_USER' || this.roleName == 'ROLE_SUPPLIER_ADMIN') {
            this.role = 2;
        } else {
            this.role = 1;
        }

        this.getAll(this.pageNo);

        this.breadcrumbService.setItems([{ 'label': 'Orders', routerLink: ['orders'] }]);
    }

    getAll(page: number) {
        this.orderService.getForTenant(page).subscribe(res => {
            console.log(res)
            res.forEach(data => {
                this.totalOrderAmount = this.totalOrderAmount + data.orderAmount;
                this.totalAmount = this.totalAmount + data.totalAmount;
                this.delivaryTotal = this.delivaryTotal + data.deliveryFees;
            })
            this.masterDtos = res;

            if (this.route.snapshot.queryParams['id']) {
                let order = this.masterDtos.filter(dto => {
                    return dto.id == this.route.snapshot.queryParams['id'];
                });

                if(order.length > 0) {
                    this.goOrderDetails(order[0]);
                } else {
                    this.messageService.add({ severity: 'info', summary: 'Error', detail: 'this order does not exist, please select from orders table', life: 3000 })
                }
            }

            this.cols = [
                { field: 'id', header: 'id' },
                { field: 'createdAt', header: 'Date' },
                { field: 'customerName', header: 'Customer Name' },
                { field: 'supplierName', header: 'Supplier Name' },
                { field: 'jobNumber', header: 'Job Number' },
                { field: 'orderAmount', header: 'Order Amount' },
                { field: 'totalAmount', header: 'Total Amount' },
                { field: 'orderStatus', header: 'Order Status' },
                { field: 'deliveryFees', header: 'Delivery Fees' }
            ];
        }, err => {
            this.messageService.add({ severity: 'error', summary: 'Server Error', detail: err.error.statusMsg, life: 3000 })
        })
    }

    loadOrders(e) {
        //console.log(e);
        if (this.masterDtos.length == 50) {
            if ((this.masterDtos.length - e.first) <= 10) {
                this.pageNo++;
                this.getAll(this.pageNo);
            }
        }
    }


    goOrderDetails(order: any) {
        sessionStorage.setItem('order', JSON.stringify(order));
        this.updateRouterLink();
        this.router.navigate(['order-details'], {queryParams: {}});
    }

    updateRouterLink() {
        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'orders';
        });

        if(page) {
            console.log('updating router>')
            page.subMenu.routerLink = 'order-details';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

    resetRouterLink() {
        console.log('resetting router>')
        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'order-details';
        });

        if(page) {
            page.subMenu.routerLink = 'orders';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

    customerOrSupplier() {
        var tenant = JSON.parse(this.authService.getStoredUser()).tenant.tenantType.id;
        return (tenant = this.garageTenant);
    }

}

