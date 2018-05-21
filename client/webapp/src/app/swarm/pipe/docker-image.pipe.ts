import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dockerImage'
})
export class DockerImagePipe implements PipeTransform {

  transform(input: any, args?: any): any {
    input = input || '';
    const found = input.indexOf('@');
    if ( found > -1 ) {
      return input.substring(0, found);
    }
    return input;
  }

}
