import { TenantService } from 'src/app/xgarage/common/service/tenant.service';
import { ServiceType } from './../../model/servicetype';
import { BrandService } from '../../../core/service/brand.service';
import { Supplier } from './../../../core/model/supplier';
import { SupplierService } from './../../../core/service/supplier.service';
import { Component, ElementRef, NgZone, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Brand } from 'src/app/xgarage/common/model/brand';
import { PartType } from '../../model/parttype';
import { ServiceTypesService } from '../../service/servicetype.service';
import { PartTypesService } from '../../service/parttype.service';
import { GoogleMap } from '@angular/google-maps';


@Component({
    selector: 'app-supplierprofile',
    templateUrl: './supplierprofile.component.html',
    styleUrls: ['../../../../demo/view/tabledemo.scss', './supplierprofile.component.scss'],
    styles: [`
  :host ::ng-deep .p-dialog .product-image {
      width: 150px;
      margin: 0 auto 2rem auto;
      display: block;
  }

  .active {border-bottom: 2px solid #6366F1 !important;border-radius: 0;}

  @media screen and (max-width: 960px) {
      :host ::ng-deep .p-datatable.p-datatable-customers .p-datatable-tbody > tr > td:last-child {
          text-align: center;
      }

      :host ::ng-deep .p-datatable.p-datatable-customers .p-datatable-tbody > tr > td:nth-child(6) {
          display: flex;
      }
  }

`],
    providers: [MessageService, ConfirmationService, DialogService]

})
export class SupplierprofileComponent implements OnInit {
    position: { lat: number, lng: number } = { lat: 0, lng: 0 };
    marker: any = {};
    mapZoom: number = 7;
    @ViewChild(GoogleMap) map: GoogleMap;

    mapConfig = {
        disableDefaultUI: true,
        fullscreenControl: true,
        zoomControl: true
    };

    initialCoordinates = {
        lat: 23.588072,
        lng: 58.382691
    };

    constructor(private fb: UntypedFormBuilder, public route: ActivatedRoute, public router: Router,
        public messageService: MessageService,
        public confirmService: ConfirmationService, private supplierService: SupplierService,
        private brandService: BrandService, private serviceTypesService: ServiceTypesService,
        private partTypesService: PartTypesService, private tenantService: TenantService,
        public breadcrumbService: AppBreadcrumbService) {

    }

    supplierForm: UntypedFormGroup;
    supplier: Supplier = {};
    brands: Brand[] = [];
    selectedBrands: Brand[] = [];
    serviceTypes: ServiceType[] = [];
    selectedServiceTypes: ServiceType[] = [];
    partTypes: PartType[] = [];
    selectedPartTypes: PartType[] = [];
    id: number = 52;
    @ViewChild('search')
    public searchElementRef: ElementRef;
    address: string;


    ngOnInit(): void {

        
        this.breadcrumbService.setItems([{'label': 'Tenants', 'routerLink': ['tenant']},{'label': 'Profile', 'routerLink': ['supplier-profile']}]);
        const supplierId = parseInt(localStorage.getItem('supplierId'), 10);

        if (supplierId) {
            this.id = supplierId
        } else {
            this.id = this.tenantService.selectedTenantId;
            localStorage.setItem('supplierId', this.id.toString());
        }

        this.supplierForm = this.fb.group({
            name: ['', Validators.required],
            cr: ['', Validators.required],
            phoneNumber: ['', [Validators.required, Validators.pattern(/^[279]\d{7}$/)]],
            email: ['', [Validators.required, Validators.email]],
            serviceTypes: ['', Validators.required],
            partTypes: ['', Validators.required],
            brand: [[], Validators.required],
            submittedBids: [{ value: '', disabled: true }],
            speciality: [''],
            enabled: [''],
        });

        this.getSupplierByIdNumber(this.id);
    }


    getAllBrands() {
        this.brandService.getAll().subscribe(res => {
            this.brands = res;
            this.selectedBrands = this.brands.filter(brand =>
                this.supplier.brand.some(selectedBrand => selectedBrand.id === brand.id));
        })
    }

    getAllServiceTypes() {
        this.serviceTypesService.getAll().subscribe(res => {
            this.serviceTypes = res;
            this.selectedServiceTypes = this.serviceTypes.filter(st =>
                this.supplier.serviceTypes.some(selectedServiceTypes => selectedServiceTypes.id === st.id));
        })
    }

    getAllPartTypes() {
        this.partTypesService.getAll().subscribe(res => {
            this.partTypes = res;
            this.selectedPartTypes = this.partTypes.filter(pt =>
                this.supplier.partTypes.some(selectedPartTypes => selectedPartTypes.id === pt.id));
        })
    }

    getSupplierByIdNumber(supplierId: number) {
        this.supplierService.getSupplierById(supplierId).subscribe((res: Supplier) => {
            this.supplier = res;
            this.getAllBrands();
            this.getAllServiceTypes();
            this.getAllPartTypes();
            this.supplierForm.setValue({
                name: res.name,
                cr: res.cr,
                phoneNumber: res.phoneNumber,
                email: res.email,
                serviceTypes: res.serviceTypes,
                partTypes: res.partTypes,
                brand: res.brand,
                submittedBids: res.submittedBids,
                speciality: res.speciality,
                enabled: res.enabled,
            });
        })
    }

    submitForm() {
        if (this.supplierForm.invalid) {
            this.messageService.add({ severity: 'error', summary: 'Erorr', detail: 'Please fill the form correctly' });
            Object.keys(this.supplierForm.controls).forEach(key => {
                this.supplierForm.controls[key].markAsTouched();
            });

            return;
        }

        this.supplierForm.addControl('id', new UntypedFormControl(this.supplier.id));
        this.supplierService.update(this.supplierForm.value).subscribe(res => {
            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Profile Updated' });
        }, err => {
            this.messageService.add({ severity: 'error', summary: 'Erorr', detail: err.error.message, life: 3000 });
        })
    }

    onMapClick(event: google.maps.MapMouseEvent) {
        //console.log(event)
        let bounds = this.map.getBounds();
        this.address = `${event.latLng!.lat()} ${event.latLng!.lng()}`;
        this.position = { lat: event.latLng!.lat(), lng: event.latLng!.lng() };

        // this.map.fitBounds(bounds!);
        this.marker = {
            position: this.position,
            title: "",
        };

        console.log(this.address)
    }
}
