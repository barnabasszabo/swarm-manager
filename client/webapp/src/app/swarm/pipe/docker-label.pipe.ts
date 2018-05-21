import { Pipe, PipeTransform } from '@angular/core';
import { Ecosystem } from '../model/Ecosystem';

@Pipe({
  name: 'dockerLabel'
})
export class DockerLabelPipe implements PipeTransform {

  transform(eco: Ecosystem, labelName: string): string[] {
    return eco.labels[labelName] || [];
  }

}
