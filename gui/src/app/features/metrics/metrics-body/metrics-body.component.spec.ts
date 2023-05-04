import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricsBodyComponent } from './metrics-body.component';

describe('MetricsBodyComponent', () => {
  let component: MetricsBodyComponent;
  let fixture: ComponentFixture<MetricsBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MetricsBodyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetricsBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
