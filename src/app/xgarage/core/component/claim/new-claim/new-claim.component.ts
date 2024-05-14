import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ClaimService } from '../../../service/claim.service';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth.service';
import { InsuranceType } from '../../../model/insurancetype';
import { Claim } from '../../../model/claim';
import domtoimage from 'dom-to-image';



@Component({
    selector: 'app-new-claim',
    templateUrl: './new-claim.component.html',
    styleUrls: ['./new-claim.component.css']
})
export class NewClaimComponent implements OnInit {

    constructor(private router: Router, private messageService: MessageService, private authService: AuthService, private breadcrumbService: AppBreadcrumbService, private claimService: ClaimService, private formBuilder: UntypedFormBuilder) { }

    newClaimForm: UntypedFormGroup = this.formBuilder.group({
        tenant: [JSON.parse(this.authService.getStoredUser()).tenant.id],
        insuranceType: ['', Validators.required],
        claimNo: ['', Validators.required],
        claimDate: [new Date().toISOString()],
        customerName: ['', Validators.required],
        contactNo: [null, Validators.required],
        excessRo: [0, Validators.required],
    });
    //s, Validators.pattern(/^[279]\d{7}$/)
    insuranceType: string[] = Object.keys(InsuranceType);;
    submitted: boolean = false;
    @Input() claimSaving: boolean;
    @Input() saving: boolean = false;
    @Input() claim: Claim;
    @Output() createClaimEvent = new EventEmitter<any>();
    @ViewChild('carImage', { read: ElementRef }) carImage: ElementRef;
    count: number = 0;
    undo = true;
    carsheetDoc: File;
    carImageSrc: any = 'assets/layout/images/car.jpg'

    ngOnInit(): void {
        if (this.claim) {
            console.log('theis is a review');
            this.newClaimForm.patchValue({
                insuranceType: this.claim.insuranceType,
                claimNo: this.claim.claimNo,
                customerName: this.claim.customerName,
                contactNo: this.claim.contactNo,
                excessRo: this.claim.excessRo + ' OMR'
            });

            for (const control in this.newClaimForm.value) {
                this.newClaimForm.get(control).disable()
            };

            this.carImageSrc = "http://letsgo-oman.com:6060/api/v1/document/" + this.claim.documents[0].name;
        }
    }

    onNewClaimFormSubmit() {
        this.submitted = true;
        this.saving = this.claimSaving;

        if (this.newClaimForm.valid && !this.undo) {
            //convert car to image first than send the request
            this.convertToImage();
        } else {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please Fill All required Fields.' });
            this.saving = false;
        }
    }

    printMousePos(event) {
        //console.log(event)
        this.count++;


        var zone = document.createElement('span');
        zone.innerText = String(this.count);
        zone.className = `zone-circle zone-${this.count}`;

        //console.log(zone)

        this.carImage.nativeElement.appendChild(zone);
        this.carImage.nativeElement.querySelector(`.zone-${this.count}`).style.left = (event.layerX - 3) + 'px';
        this.carImage.nativeElement.querySelector(`.zone-${this.count}`).style.top = event.layerY + 'px';

        this.undo = false;
    }

    undoMousePos() {
        if (this.carImage.nativeElement.querySelector(`.zone-${this.count}`)) {
            this.carImage.nativeElement.querySelector(`.zone-${this.count}`).remove();
            this.count--;

            if (this.count == 0) {
                this.undo = true;
            }

        }
    }

    convertToImage() {
       // console.log(this.carsheetDoc)
        if(this.carsheetDoc) {
            console.log('carsheet exisits alraedy')
            this.createClaimEvent.emit({ form: this.newClaimForm.value, carsheet: this.carsheetDoc });
        } else {
            console.log('new carsheet')

            var node = this.carImage.nativeElement;
            var img;

            domtoimage.toJpeg(node, { bgcolor: '#fff' }).then(
                (dataUrl: string) => {
                    img = new Image();
                    img.src = dataUrl;

                    //console.log(dataUrl)

                    var arr = dataUrl.split(','),
                        //mime = arr[0].match(/:(.*?);/)[1],
                        bstr = atob(arr[arr.length - 1]),
                        n = bstr.length,
                        u8arr = new Uint8Array(n);
                    while (n--) {
                        u8arr[n] = bstr.charCodeAt(n);
                    }

                    this.carsheetDoc = new File([u8arr], 'carsheet.jpeg');
                    //emit save claim event
                    this.createClaimEvent.emit({ form: this.newClaimForm.value, carsheet: this.carsheetDoc });
                }
            )
        }

    }

}
