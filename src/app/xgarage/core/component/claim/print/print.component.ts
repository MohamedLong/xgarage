import { Component, Input } from '@angular/core';
import { Claim } from '../../../model/claim';

@Component({
    selector: 'app-print',
    templateUrl: './print.component.html',
    styleUrls: ['./print.component.scss'],
    styles: [`
        .layout-invoice-page {
            width: auto;
    display: block!important;
        }
    `]
})
export class PrintComponent {
    //@Input() claim: Claim;
    claim: Claim = JSON.parse(sessionStorage.getItem('claim'));

    print() {
        setTimeout(function () {
            window.print();
        }, 1000)
    }

}
