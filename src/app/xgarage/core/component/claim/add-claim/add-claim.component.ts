import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { ClaimService } from '../../../service/claim.service';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/auth/services/auth.service';
import { MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Claim } from '../../../model/claim';
import { StatusService } from 'src/app/xgarage/common/service/status.service';
import domtoimage from 'dom-to-image';
import { GenericDetailsComponent } from 'src/app/xgarage/common/generic/genericdetailscomponent';
import jsPDF from 'jspdf';

interface Tick {
    id: number, name: string, remarks?: string
}

@Component({
    selector: 'app-add-claim',
    templateUrl: './add-claim.component.html',
    styles: [
        `
    .wizard-card {width: 100% !important}
    .wizard-body {background: none; height: unset;}
    .wizard-card, .wizard-card-header {border-radius: 5px;}
    .tab:first-of-type {
        border-top-left-radius: 5px
    }
    .tab:last-of-type {
        border-top-right-radius: 5px
    }
    .wizard-body .wizard-wrapper .wizard-content {
        height: auto;
        min-height: auto;
    }

    .wizard-body .wizard-wrapper .wizard-content .wizard-card {
        height: 100%;
    }

    `
    ],
    providers: [MessageService]
})


export class AddClaimComponent extends GenericDetailsComponent implements OnInit {

    constructor(public router: Router, public route: ActivatedRoute,
        public messageService: MessageService,
        public breadcrumbService: AppBreadcrumbService,
        private claimService: ClaimService,
        private formBuilder: UntypedFormBuilder,
        public statusService: StatusService) {
        super(route, router, null, null, null, breadcrumbService);
    }

    activeTab = 'car-info';

    car: any;
    ticks: Tick[] = [];
    selectedTicks: Tick[] = [];
    claimForm: UntypedFormGroup = this.formBuilder.group({
        id: [''],
        excDeliveryDate: [''],
        breakDown: [''],
        km: [''],
        claimDate: [''],
        receivedDate: [''],
        claimTicks: [[]]
    });
    saving: boolean = false;
    saved: boolean = false;
    isBreakdown: boolean = false;
    submitted: boolean = false;
    minExcDeliveryDate: Date;
    claim: Claim;
    count: number = 0;
    undo = true;
    carsheetDoc: File;
    carImageSrc: any = 'assets/layout/images/car.jpg'
    @ViewChild('secondCar', { read: ElementRef }) secondCar: ElementRef;
    @ViewChild('toPrint') toPrint: ElementRef;

    ngOnInit(): void {
        this.onGetJobTicks();
        this.route.queryParamMap.subscribe(params => {
            if (params.has('update')) {
                this.breadcrumbService.setItems([{ 'label': 'Claims', routerLink: ['claims'] }, { 'label': 'Claim Details', routerLink: ['claim-details'] }, { 'label': 'Add Claim', routerLink: ['add-claim'] }]);
                if (sessionStorage.getItem('claim')) {
                    this.activeTab = 'create-claim';
                    this.claim = JSON.parse(sessionStorage.getItem('claim'));

                    this.claimForm.patchValue({
                        id: this.claim.id,
                        excDeliveryDate: this.claim.excDeliveryDate ? new Date(this.claim.excDeliveryDate) : '',
                        breakDown: this.claim.breakDown ? new Date(this.claim.breakDown) : '',
                        km: this.claim.km,
                        claimDate: this.claim.claimDate ? new Date(this.claim.claimDate) : '',
                    });

                    // check if breakdown is null or not
                    if (this.claim.breakDown) {
                        this.isBreakdown = true;
                    }

                    if (this.claim.documents[1]) {
                        this.undo = false;
                        this.carImageSrc = "http://letsgo-oman.com:6060/api/v1/document/" + this.claim.documents[1].name;
                    }
                }
            } else {
                this.breadcrumbService.setItems([{ 'label': 'Claims', routerLink: ['claims'] }, { 'label': 'Add Claim', routerLink: ['add-claim'] }]);
            }
        })
    }

    printdiv() {
        var divContents = document.getElementById("print").innerHTML;
        var a = window.open('', '');
        a.document.write('<html>');
        a.document.write('<body >');
        a.document.write(divContents);
        a.document.write('</body></html>');
        a.document.close();
        setTimeout(function () {
            a.print();
        }, 1000);
    }

    onisBreakdownCheckedChanged(e) {
        //console.log(e)
        this.isBreakdown = e.checked;
        if (!this.isBreakdown) {
            this.claimForm.get('breakDown').setValue('');
        } else {
            this.claim.breakDown ? this.claimForm.get('breakDown').setValue(new Date(this.claim.breakDown)) : ''
        }
    }

    onTickRemarkAdd(tick: Tick) {
        //console.log({tick})

        let isTickAlreadyChecked = this.claimForm.get('claimTicks').value.find(oldTick => {
            //console.log(oldTick.tick.id, tick.id)
            return oldTick.tick.id == tick.id
        });

        //console.log(isTickAlreadyChecked)

        if (isTickAlreadyChecked) {
            console.log('found')
            isTickAlreadyChecked.remarks = tick.remarks;
        };

        //console.log(this.claimForm.get('claimTicks').value);
    }

    onGetJobTicks() {
        this.claimService.getClaimTicks().subscribe(res => {
            console.log(res)
            this.ticks = res;
            this.ticks.forEach(tick => {
                if (!tick.remarks) {
                    tick.remarks = "";
                }

            });

            if (this.claim && this.claim.claimTicks.length > 0 && this.ticks.length > 0) {
                //console.log('ticks')
                this.claim.claimTicks.forEach(claimTick => {
                    this.ticks.forEach(tick => {
                        if (claimTick.tick.id == tick.id) {
                            tick.remarks = claimTick.remarks;
                        }
                    });

                    let foundTick = this.ticks.find(tick => {
                        return tick.id == claimTick.tick.id
                    });

                    console.log(foundTick)

                    this.selectedTicks.push(foundTick);
                    this.claimForm.get('claimTicks').setValue([...this.claimForm.get('claimTicks').value, { claim: { id: this.claim.id }, tick: { id: foundTick.id }, remarks: foundTick.remarks }]);
                });
            }

            //console.log(this.selectedTicks, this.claimForm.get('claimTicks').value)
        })
    }

    onTicksChange(tick: Tick) {
        //console.log(tick, event)
        //if (!event.checked) {

        let isTickFound = this.claimForm.get('claimTicks').value.filter(claimTick => {
            return claimTick.tick.id == tick.id;
        });

        //console.log({isTickFound})

        //check if tick is checked before uncheck
        if (isTickFound.length > 0) {
            //console.log('tick is found')
            this.claimForm.get('claimTicks').setValue(
                this.claimForm.get('claimTicks').value.filter(val => {
                    return val.tick.id !== tick.id;
                })
            );

            //console.log(this.claimForm.get('claimTicks').value)
        } else {
            this.claimForm.get('claimTicks').setValue([...this.claimForm.get('claimTicks').value, { claim: { id: this.claim.id }, tick: { id: tick.id }, remarks: tick.remarks }]);
            //console.log(this.claimForm.get('claimTicks').value)
        }

    }

    onUpdateCalim() {
        console.log('updating claim>>>')

        this.claimForm.get('id').setValue(this.claim.id);
        let datetime = new Date(this.claimForm.get('claimDate').value).toISOString();
        let updatedClaimForm = this.claimForm.value;
        updatedClaimForm.claimDate = datetime;
        updatedClaimForm.receivedDate = datetime;

        //check if status is already updated & update status if not
        if (this.claim.status.id == 1) {
            //console.log('update status')
            updatedClaimForm.status = this.statusService.statuses.find(status => { return status.id == 13 });
        } else {
            //console.log('dont update status')
            updatedClaimForm.status = this.claim.status;
        }

        let claimBody = {
            claim: updatedClaimForm,
            claimPartsDtoList: []
        }

        //console.log(updatedClaimForm)

        let stringUpdatedClaimBody = JSON.stringify(claimBody);
        let UpdatedClaimFormData = new FormData();

        UpdatedClaimFormData.append('claimBody', stringUpdatedClaimBody);
        UpdatedClaimFormData.append('claimDocument', this.carsheetDoc ? this.carsheetDoc : null);

        this.claimService.updateClaim(UpdatedClaimFormData).subscribe(res => {
            //console.log(res)
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Claim Updated Succefully. Redirecting To Claim..', life: 2000 });
            this.saving = false;
            this.saved = true;
            //this.claimForm.reset('');
            setTimeout(() => {
                this.goToClaimDetails(res.id);
            }, 1000)

        }, err => {
            //console.log(err)
            this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.error });
            this.saving = false;
        });
    }

    goToClaimDetails(id: number) {
        sessionStorage.setItem('claimId', JSON.stringify(id));
        this.router.navigate(['claim-details']);
    }

    onRecievedDateSelect(val: any) {
        this.claimForm.get('excDeliveryDate').setValue('');
        this.minExcDeliveryDate = new Date(val);
        this.minExcDeliveryDate.setDate(this.minExcDeliveryDate.getDate() + 1);
    }

    printMousePos(event) {
        //console.log(event)
        this.count++;

        var zone = document.createElement('span');
        zone.innerText = String(this.count);
        zone.className = `zone-circle zone-${this.count}`;

        //console.log(zone)
        this.secondCar.nativeElement.appendChild(zone);

        let left = (event.clientX - 3) - this.secondCar.nativeElement.getBoundingClientRect().left;
        let top = event.clientY - this.secondCar.nativeElement.getBoundingClientRect().top;

        let positionLeft = (left / event.srcElement.clientWidth) * 100;
        let positionTop= (top / event.srcElement.clientHeight) * 100;
        //console.log({positionLeft}, {positionTop});

        this.secondCar.nativeElement.querySelector(`.zone-${this.count}`).style.left = positionLeft + '%';
        this.secondCar.nativeElement.querySelector(`.zone-${this.count}`).style.top = positionTop + '%';

        this.undo = false;
    }

    undoMousePos() {
        if (this.secondCar.nativeElement.querySelector(`.zone-${this.count}`)) {
            this.secondCar.nativeElement.querySelector(`.zone-${this.count}`).remove();
            this.count--;

            if (this.count == 0) {
                this.undo = true;
            }

        }
    }

    convertToImage() {
        this.submitted = true;
        //console.log(this.claimForm.value)
        if (this.claimForm.valid) {
            //console.log('converting img>>>')
            this.saving = true;

            if (this.claim.documents[1] || this.count == 0) {
                console.log('no car image sheet')
                this.onUpdateCalim();
            } else {
                console.log('car image sheet')
                var node = this.secondCar.nativeElement;
                var img;

                domtoimage.toPng(node, { bgcolor: '#fff' }).then(
                    (dataUrl: string) => {
                        img = new Image();
                        img.src = dataUrl;

                        //console.log(dataUrl)

                        var arr = dataUrl.split(','),
                        // mime = arr[0].match(/:(.*?);/)[1],
                        bstr = atob(arr[arr.length - 1]),
                        n = bstr.length,
                        u8arr = new Uint8Array(n);
                        while (n--) {
                        u8arr[n] = bstr.charCodeAt(n);
                        }

                        // this.carImageSrc = dataUrl;
                        this.carsheetDoc = new File([u8arr], 'carsheet.png');
                        this.onUpdateCalim();
                    }
                )
            }

        } else {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Some Fields Are Not Valid, Please Try Again.' });
            this.saving = false;
        }

    }

}
