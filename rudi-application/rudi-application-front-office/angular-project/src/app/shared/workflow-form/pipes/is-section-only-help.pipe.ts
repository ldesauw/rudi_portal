import {Pipe, PipeTransform} from '@angular/core';
import {Section} from '@app/projekt/projekt-api';

@Pipe({
    name: 'isSectionOnlyHelp'
})
export class IsSectionOnlyHelpPipe implements PipeTransform {
    transform(section: Section): boolean {
        return section != null && section.help != null && section.help !== '' && (section.label == null || section.label === '');
    }
}
