import { Status } from 'src/app/xgarage/common/model/status';
import { Injectable } from '@angular/core';
import { config } from 'src/app/config';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { GenericService } from '../../common/generic/genericservice';
import { Job } from '../model/job';
import { MessageResponse } from '../../common/dto/messageresponse';

@Injectable({
    providedIn: 'root'
})
export class JobService extends GenericService<Job> {

    constructor(protected http: HttpClient) {
        super(http, config.coreApiUrl + '/job');
    }

    getJobByClaimNumber(cno: string) {
        return this.http.get<any>(config.coreApiUrl + '/job/claimNo/' + cno);
    }

    saveJob(body: any) {
        return this.http.post<any>(config.coreApiUrl + '/job/saveJob?jobBody=' + body, '').pipe(
            tap(res => {
                return res
            }), catchError(err => {
                return throwError(err)
            })
        )
    }

    cancelJob(jobId: number, status: Status) {
        return this.http.post<MessageResponse>(config.coreApiUrl + '/job/changeStatus/' + jobId, status);

    }

    searchlJob(searchTerm: string) {
        return this.http.get<any[]>(config.coreApiUrl + '/job/brandOrPartNameLike/' + searchTerm).pipe(
            tap(res => {
                return res
            }), catchError(err => {
                return throwError(err)
            })
        )
    }

    partialUpdate(dto: any) {
        return this.http.patch<MessageResponse>(config.coreApiUrl + '/job/updateJob', dto);
    }

    getBidsByJob(page: number) {
        let endpoint = page? `/job/tenantSupplier?pageNo=${page}&pageSize=200` : '/job/tenantSupplier';
        return this.http.get<any[]>(config.coreApiUrl + endpoint);
    }

}
