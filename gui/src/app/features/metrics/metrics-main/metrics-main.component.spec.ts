import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricsMainComponent } from './metrics-main.component';

describe('MetricsMainComponent', () => {
  let component: MetricsMainComponent;
  let fixture: ComponentFixture<MetricsMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MetricsMainComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetricsMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
