import {Component, Input, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {DialogMemberOrganizationService} from '@core/services/organization/dialog-member-organization.service';
import {OrganizationMetierService} from '@core/services/organization/organization-metier.service';
import {ALL_TYPES} from '@shared/models/title-icon-type';
import {Organization} from 'micro_service_modules/strukture/strukture-model';
import {OrganizationTableDialogData} from './organization-table-dialog-data';

@Component({
    selector: 'app-organization-table',
    templateUrl: './organization-table.component.html',
    styleUrls: ['./organization-table.component.scss']
})
export class OrganizationTableComponent implements OnInit {
    @Input() isLoading: boolean;
    @Input() organization: Organization;
    @Input() enableCaptchaOnPage: boolean;
    displayedColumns: string[] = ['name', 'id', 'dash'];
    dataSource: MatTableDataSource<Organization> = new MatTableDataSource([]);


    constructor(private readonly organizationService: OrganizationMetierService,
                private readonly iconRegistryService: IconRegistryService,
                private readonly dialogMemberOrganizationService: DialogMemberOrganizationService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
        this.dataSource = new MatTableDataSource([this.organization]);
    }

    /**
     * Action déclenchée lors du clic sur les trois petits points , ouverture de popin
     * @param organization
     */
    handleClickChangePassword(organization: Organization): void {
        const organizationTableDialogData: OrganizationTableDialogData = {
            enableCaptchaOnPage: this.enableCaptchaOnPage,
            organizationUuid: this.organization.uuid
        };
        this.dialogMemberOrganizationService.openDialogUpdatepassword(organizationTableDialogData).subscribe();
    }
}
