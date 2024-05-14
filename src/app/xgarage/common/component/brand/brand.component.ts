import { DatePipe } from '@angular/common';
import { GenericComponent } from './../../../common/generic/genericcomponent';
import { AppBreadcrumbService } from '../../../../app.breadcrumb.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Component, OnInit } from '@angular/core';
import { BrandService } from '../../service/brand.service';
import { Brand } from '../../model/brand';
import { AuthService } from 'src/app/auth/services/auth.service';

@Component({
    selector: 'app-barnd',
    templateUrl: './brand.component.html', styleUrls: ['../../../../demo/view/tabledemo.scss'],
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
export class BrandComponent extends GenericComponent implements OnInit {

    constructor(public route: ActivatedRoute, private authService: AuthService ,private router: Router, private brandService: BrandService,
        public messageService: MessageService, public datePipe: DatePipe, breadcrumbService: AppBreadcrumbService) {
        super(route, datePipe, breadcrumbService);
    }

    valid: boolean = false;

    ngOnInit(): void {
        this.getAll();
        super.callInsideOnInit();

        this.breadcrumbService.setItems([{'label': 'Brands', 'routerLink': ['brands']}]);
    }

    getAll() {
        this.brandService.getAll().subscribe({
            next: (masters) => {
                this.masters = masters;
                this.loading = false;
                this.cols = [
                    { field: 'id', header: 'ID' },
                    { field: 'brandName', header: 'Brand Name' },
                ];
            },
            error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
        });
    }

    save() {
        this.submitted = true;
        if (this.master.brandName) {
            this.master.updatedBy = JSON.parse(this.authService.getStoredUser()).id;
            this.master.updatedAt = new Date();
            if (this.master.id) {
                // @ts-ignore
                this.brandService.update(this.master).subscribe(
                    {
                        next: (data) => {
                            this.master = data;
                            this.masters[this.findIndexById(this.master.id, this.masters)] = this.master;
                            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Brand Updated'});
                            this.getAll();
                        },
                        error: (e) => alert(e)
                    }
                );
            } else {
                this.brandService.add(this.master).subscribe(
                    {
                        next: (data) => {
                            this.master = data;
                            this.masters.push(this.master);
                            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Brand created successfully' });
                        },
                        error: (e) => alert(e)
                    }
                );
            }
            this.masters = [...this.masters];
            this.masterDialog = false;
            this.master = {};
        }
    }

    confirmDelete() {
        this.brandService.delete(this.master.id).subscribe(res => {
            this.messageService.add({ severity: 'success', summary: 'Brand deleted successfully' });
            this.deleteSingleDialog = false;
            this.masters = this.masters.filter(val => val.id != this.master.id);
        }, err => {
            this.messageService.add({ severity: 'error', summary: 'Erorr', detail: err, life: 3000 });
        })
    }

}
