import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { config } from "src/app/config";
import { GenericService } from "../generic/genericservice";
import { PartType } from "../model/parttype";

@Injectable({
    providedIn: 'root'
})
export class PartTypesService extends GenericService<PartType>{
    constructor(http: HttpClient){
        super(http, config.coreApiUrl + '/partTypes');
    }

}
