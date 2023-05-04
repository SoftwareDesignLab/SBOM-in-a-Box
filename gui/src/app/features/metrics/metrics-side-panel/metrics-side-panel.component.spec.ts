import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricsSidePanelComponent } from './metrics-side-panel.component';

describe('MetricsSidePanelComponent', () => {
  let component: MetricsSidePanelComponent;
  let fixture: ComponentFixture<MetricsSidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MetricsSidePanelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetricsSidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
