import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'removeMsgAsString'
})
export class RemoveMsgAsStringPipe implements PipeTransform {

  transform(value: string[], args?: any): any {
    let result = '';
    if (value) {
      value.forEach(msg => {
        result += '<div>' + msg + '</div>';
      });
    }
    return result;
  }

}
