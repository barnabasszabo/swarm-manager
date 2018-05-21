import { Pipe, PipeTransform } from '@angular/core';
import { Ecosystem } from '../model/Ecosystem';

@Pipe({
  name: 'ecosystemPorts'
})
export class EcosystemPortsPipe implements PipeTransform {

  transform(ecosystems: Ecosystem[], args?: any): any[] {
    const result = [];

    ecosystems.forEach(eco => {
      eco.portConfig.forEach(port => {
        result.push({ name: eco.name, port: port });
      });
    });
    // tslint:disable-next-line:max-line-length
    result.sort(function (a, b) { return (a.port.PublishedPort > b.port.PublishedPort) ? 1 : ((b.port.PublishedPort > a.port.PublishedPort) ? -1 : 0); });
    return result;
  }

}
