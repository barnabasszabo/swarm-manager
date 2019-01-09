import { Pipe, PipeTransform } from '@angular/core';
import { SwarmTask } from '../model/SwarmTask';

@Pipe({
  name: 'nanoCPUFormatter'
})
export class NanoCPUFormatterPipe implements PipeTransform {

  transform(task: SwarmTask, type: string): number {
    try {
      if ( type === 'limit' ) {
        return parseFloat((task.task.Spec.Resources.Limits.NanoCPUs / 1000000000).toFixed(2));
      } else {
        return parseFloat((task.task.Spec.Resources.Reservations.NanoCPUs / 1000000000).toFixed(2));
      }
    } catch (e) {
      return 0;
    }
  }

}
