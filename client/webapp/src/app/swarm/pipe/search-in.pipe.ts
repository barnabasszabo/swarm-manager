import { Pipe, PipeTransform } from '@angular/core';
import { Ecosystem } from '../model/Ecosystem';

@Pipe({
  name: 'searchIn'
})
export class SearchInPipe implements PipeTransform {

  transform(ecosystems: Ecosystem[], searchStr?: string): any {
    console.log('searchIn - searchStr=', searchStr);
    if ( searchStr ) {
      return ecosystems.filter(eco => JSON.stringify(eco).toLowerCase().indexOf(searchStr.toLowerCase()) > -1 );
    }
    return ecosystems;
  }

}
