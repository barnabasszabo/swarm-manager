import { Component, OnInit } from '@angular/core';
import { SwarmService } from '../swarm.service';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss']
})
export class FaqComponent implements OnInit {

  faqHtml = '';

  constructor(private swarmService: SwarmService) { }

  ngOnInit() {
    this.swarmService.getFaq().subscribe(data => {
      this.faqHtml = data.value;
    });
  }

}
