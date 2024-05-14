import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SupplierService } from 'src/app/xgarage/core/service/supplier.service';
import { Observable } from 'rxjs';
import { TenantService } from '../../service/tenant.service';
import { Supplier } from 'src/app/xgarage/core/model/supplier';
import { UntypedFormControl } from '@angular/forms';

@Component({
    selector: 'app-privacy',
    templateUrl: './privacy.component.html',
})
export class PrivacyComponent implements OnInit {

    constructor(private supplierService: SupplierService, private tenantService: TenantService) { }

    privateSuppliersList: any[] = [];
    selectedPrivateSuppliers: Observable<any>;
    displayPrivateSuppliers: boolean = false;
    supplierSelected: boolean = false;
    @Input() id: number;
    @Input() type: string;
    @Input() privacyControl: UntypedFormControl;
    @Input() suppliersControl: UntypedFormControl;
    @Input() label: string;
    @Input() selectionList: any;
    @Output() enableBidding: EventEmitter<boolean> = new EventEmitter();
    multipleSelect: boolean;

    ngOnInit(): void {
        // if(typeof this.suppliersControl.value == 'number') {
        //     console.log(this.suppliersControl.value)
        //     if(this.suppliersControl.value > 0) {

        //     }
        // }

    }

    onSelectChange(value: string) {
        //console.log('select changed')
        this.privateSuppliersList = [];
        this.suppliersControl.reset(this.suppliersControl.value);

        if (value == 'Private' || value == 'Direct') {
            //console.log('value is', value)
            this.getSuppliers();
            this.displayPrivateSuppliers = true;
            this.enableBidding.emit(false);

            if (value == 'Private') {
                this.privacyControl.setValue('Private');
                this.multipleSelect = true;
            } else {
                this.privateSuppliersList = [];
                this.multipleSelect = false
            };
        } else {
            this.enableBidding.emit(true);
            this.displayPrivateSuppliers = false;
        }

        // console.log(this.privateSuppliersList)
    }

    getSuppliers() {
        //console.log(this.type, this.id, this.label)
        if (this.id) {
            this.type == 'job' ?
                this.selectedPrivateSuppliers = this.supplierService.getSupplierByBrandId(this.id)
                :
                this.selectedPrivateSuppliers = this.tenantService.getTenantsByType(2)
        }
    }

    selectSupplier(value: Supplier[]) {
        //check if at least 1 supplier is slected
        //console.log(value)
        if (value.length > 0 && this.multipleSelect) {
            this.supplierSelected = true;
        } else if (value.length > 0 && !this.multipleSelect) {
            this.suppliersControl.setValue(value[0].id);
            //console.log(this.suppliersControl)
            this.supplierSelected = true;
        }
        else {
            this.supplierSelected = false;
        }

        this.privateSuppliersList = value;
    }

    removePrivateSupplier(value: { id: any; }) {
        // console.log(this.suppliersControl.value)
        if (this.multipleSelect) {
            let updatedPrivateSuppliers = this.suppliersControl.value.filter(supplier => {
                return supplier.id !== value.id;
            });
            this.suppliersControl.setValue(updatedPrivateSuppliers);
            this.privateSuppliersList = updatedPrivateSuppliers;

            if (this.suppliersControl.value.length == 0) {
                this.privacyControl.setValue('Public');
                this.supplierSelected = false;
            }
        } else {
            this.enableBidding.emit(true);
            this.privacyControl.setValue('Bidding');
            this.privateSuppliersList = [];
        }

    }

    resetPrivacy() {
       // console.log('privacy is resetted')
        //console.log(this.suppliersControl.value, this.privacyControl.value)
        if (this.privateSuppliersList.length == 0) {
            //----->
            this.suppliersControl.setValue([]);
            this.privacyControl.setValue('Public');

            this.enableBidding.emit(true);
            //----->
        }
    }

}
