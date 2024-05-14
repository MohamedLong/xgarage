import { AuthService } from '../../../../auth/services/auth.service';
import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { GenericComponent } from 'src/app/xgarage/common/generic/genericcomponent';
import { Status } from 'src/app/xgarage/common/model/status';
import { JobService } from '../../service/job.service';
import { InsuranceType } from '../../model/insurancetype';
import { UpdateJobDto } from '../../dto/updatedjobdto';
import { StatusConstants } from '../../model/statusconstatnts';


@Component({
    selector: 'app-job',
    templateUrl: './job.component.html',
    styleUrls: ['../../../../demo/view/tabledemo.scss'],
    styles: ['.active {border-bottom: 2px solid #6366F1 !important;border-radius: 0;}'],
    providers: [MessageService, ConfirmationService, DatePipe]
})
export class JobComponent extends GenericComponent implements OnInit {

    constructor(public route: ActivatedRoute, private authService: AuthService,
        private router: Router, private jobService: JobService,
        public messageService: MessageService, public datePipe: DatePipe, breadcrumbService: AppBreadcrumbService) {
        super(route, datePipe, breadcrumbService);
    }

    role: number = this.authService.isLoggedIn() ? JSON.parse(this.authService.getStoredUser()).roles[0].id : 0;
    selectedStatus: Status = {};
    statuses: Status[];
    valid: boolean = false;
    insuranceTypes = Object.values(InsuranceType);
    selectedInsuranceType: string;
    jobDto: UpdateJobDto = {};
    fillteredDto: any[] = [];
    status: any[] = ["All"];
    selectedState = 'All';
    pageNo: number = 0;
    ngOnInit(): void {
        this.resetRouterLink();

        //review this line
        if (localStorage.getItem('jobId') || localStorage.getItem('bidView')) {
            localStorage.removeItem('jobId');
            localStorage.removeItem('bidView');
        }

        if (this.route.snapshot.queryParams['id']) {
            this.goDetails(this.route.snapshot.queryParams['id']);
        } else if (sessionStorage.getItem('jobId') || localStorage.getItem('bidView')) {
            sessionStorage.removeItem('jobId');
            localStorage.removeItem('bidView');
        }

        super.callInsideOnInit();
        this.getAllForTenant(this.pageNo);
        //console.log(this.role)
        this.breadcrumbService.setItems([{ 'label': 'Requests', routerLink: ['jobs'] }]);
    }

    getAllForTenant(page: number) {
        let user = this.authService.getStoredUser();
        if (JSON.parse(user).tenant) {
            this.jobService.getForTenant(page).subscribe({
                next: (data) => {
                    this.masterDtos = data.reverse();
                    this.masterDtos = this.masterDtos.filter(job => job.id != null);
                    this.cols = [
                        { field: 'jobNo', header: 'Job Number' },
                        { field: 'jobTitle', header: 'Job Tilte' },
                        { field: 'partNames', header: 'Part Names' },
                        { field: 'submittedBids', header: 'No. Of Submitted Bids' },
                        { field: 'jobStatus', header: 'Job Status' },
                        { field: 'claimNo', header: 'Claim Number' }
                    ];
                    this.loading = false;
                    this.fillteredDto = this.masterDtos;
                    this.setStatusNames(this.masterDtos)
                },
                error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
            });
        } else {
            this.jobService.getAll().subscribe({
                next: (data) => {
                    this.masterDtos = data;
                    this.masterDtos = this.masterDtos.filter(job => job.id != null);
                    this.loading = false;
                },
                error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
            });
        }
    }

    getAllForUser() {
        let user = this.authService.getStoredUser();
        if (JSON.parse(user).tenant) {
            this.jobService.getForUser().subscribe({
                next: (masters) => {
                    this.masters = masters;
                    this.loading = false;
                },
                error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
            });
        } else {
            this.jobService.getAll().subscribe({
                next: (masters) => {
                    this.masters = masters;
                    this.loading = false;
                },
                error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
            });
        }
    }

    editParentAction(dto: any) {
        this.jobDto.id = dto.id;
        this.jobDto.jobNumber = dto.jobNo;
        this.jobDto.status = dto.status;
        this.masterDialog = true;
    }

    updateParent() {
        this.submitted = true;
        if (this.jobDto.jobNumber) {
            this.jobService.partialUpdate(this.jobDto).subscribe(
                {
                    next: (data) => {
                        if (data.messageCode == 200) {
                            this.masterDtos.find(job => job.id == this.jobDto.id).jobNo = this.jobDto.jobNumber;
                            this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Job Updated', life: 3000 });
                            this.masterDialog = false;
                        } else {
                            this.messageService.add({ severity: 'error', summary: 'Server Error', detail: data.message, life: 3000 })
                        }
                    },
                    error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
                }
            );
        }
    }

    hideParentDialog() {
        this.masterDialog = false;
        this.submitted = false;
        this.editable = false;
    }

    confirmDelete() {
        let cancelStatus: Status = {
            id: StatusConstants.CANCELED_STATUS
        }
        this.jobService.cancelJob(this.master.id, cancelStatus).subscribe(res => {
            if (res.messageCode == 200) {
                this.messageService.add({ severity: 'success', summary: 'Job cancelled successfully' });
                this.deleteSingleDialog = false;
                this.getAllForTenant(this.pageNo);
            }
            else {
                this.messageService.add({ severity: 'error', summary: 'Erorr', detail: 'Could Not Cancel Job', life: 3000 });
            }
        }, err => {
            this.messageService.add({ severity: 'error', summary: 'Erorr', detail: err.error.message, life: 3000 });
        })
    }


    goDetails(dto: any) {
        // localStorage.setItem('jobId', dto.id);
        dto.id? sessionStorage.setItem('jobId', dto.id) : sessionStorage.setItem('jobId', dto);
        this.updateRouterLink();
        this.router.navigate(['job-details']);
    }

    loadRequests(e) {
        // console.log(e);
        if (this.masterDtos.length == 100) {
            if ((this.masterDtos.length - e.first) <= 10) {
                this.pageNo++;
                console.log(this.pageNo)
                this.getAllForTenant(this.pageNo);
            }
        }
    }

    setStatusNames(arr) {
        let names = [];

        arr.forEach(element => {
            names.push(element.jobStatus);
        });

        if (names.length > 0) {
            names.forEach((name, index) => {
                if (!this.status.includes(name)) {
                    this.status.push(name);
                }
            });
        }
    }

    filterByStatus(state: any) {
        console.log('state: ', state);
        this.selectedState = state;
        if (state == 'All') {
            this.fillteredDto = this.masterDtos;
        } else {
            this.fillteredDto = this.masterDtos.filter(master => master.jobStatus == state);
        }
    }

    updateRouterLink() {
        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'jobs';
        });

        if(page) {
            console.log('updating router>>')
            page.subMenu.routerLink = 'job-details';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

    resetRouterLink() {

        let subs = JSON.parse(localStorage.getItem('subs'));

        let page = subs.find(sub => {
            return sub.subMenu.routerLink == 'job-details';
        });

        if(page) {
            console.log('resetting router>>')
            page.subMenu.routerLink = 'jobs';
        }

        localStorage.setItem('subs', JSON.stringify(subs));
    }

}
