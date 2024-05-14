import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { config } from "../../../config";
import { GenericService } from '../../common/generic/genericservice';

@Injectable({
    providedIn: 'root'
})

export class WorkshopGradeService extends GenericService<any> {

    constructor(http: HttpClient) {
        super(http, config.coreApiUrl + '/workshopgrade');
     }

    getWorkshopGrade() {
        return this.http.get<any[]>(config.coreApiUrl + '/workshopgrade/all?pageNo=0&pageSize=50');

    }
}
