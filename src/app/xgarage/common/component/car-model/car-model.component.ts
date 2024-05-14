import { DatePipe } from '@angular/common';
import { GenericComponent } from './../../../common/generic/genericcomponent';
import { AppBreadcrumbService } from '../../../../app.breadcrumb.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Component, OnInit } from '@angular/core';
import { CarModelService } from '../../service/carmodel.service';
import { CarModel } from '../../model/carmodel';
import { AuthService } from 'src/app/auth/services/auth.service';

@Component({
    selector: 'app-car-model',
    templateUrl: './car-model.component.html', styleUrls: ['../../../../demo/view/tabledemo.scss'],
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
export class CarModelComponent extends GenericComponent implements OnInit {

    constructor(public route: ActivatedRoute, private authService: AuthService ,private router: Router, private carModelService: CarModelService,
        public messageService: MessageService, public datePipe: DatePipe, breadcrumbService: AppBreadcrumbService) {
        super(route, datePipe, breadcrumbService);
    }

    valid: boolean = false;

    ngOnInit(): void {
        this.getAll();
        super.callInsideOnInit();
    }

    getAll() {
        this.carModelService.getAll().subscribe({
            next: (masters) => {
                this.masters = masters;
                this.loading = false;
                this.cols = [
                    { field: 'id', header: 'ID' },
                    { field: 'name', header: 'Car Model Name' },
                ];
            },
            error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
        });
    }

    save() {
        this.submitted = true;
        if (this.master.name) {
            this.master.updatedBy = JSON.parse(this.authService.getStoredUser()).id;
            this.master.updatedAt = new Date();
            if (this.master.id) {
                // @ts-ignore
                this.carModelService.update(this.master).subscribe(
                    {
                        next: (data) => {
                            this.master = data;
                            this.masters[this.findIndexById(this.master.id, this.masters)] = this.master;
                            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Car Model Updated'});
                            this.getAll();
                        },
                        error: (e) => alert(e)
                    }
                );
            } else {
                this.carModelService.add(this.master).subscribe(
                    {
                        next: (data) => {
                            this.master = data;
                            this.masters.push(this.master);
                            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Car Model created successfully' });
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
        this.carModelService.delete(this.master.id).subscribe(res => {
            this.messageService.add({ severity: 'success', summary: 'Car Model deleted successfully' });
            this.deleteSingleDialog = false;
            this.masters = this.masters.filter(val => val.id != this.master.id);
        }, err => {
            this.messageService.add({ severity: 'error', summary: 'Erorr', detail: err, life: 3000 });
        })
    }

}
