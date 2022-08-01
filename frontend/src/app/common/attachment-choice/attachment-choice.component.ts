import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { faFile } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-attachment-choice',
  templateUrl: './attachment-choice.component.html',
  styleUrls: ['./attachment-choice.component.css']
})
export class AttachmentChoiceComponent implements OnInit {
  @Output("attachment") attachmentEvent = new EventEmitter<String>();
  file?: File;
  @ViewChild('fileSelect', {static: true}) fileSelect?: ElementRef;
  faFile = faFile;

  constructor() { }

  ngOnInit(): void {  }

  selectFile(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if(fileList && fileList.length > 0) {
      let firstFile = fileList.item(0);
      this.file = firstFile ? firstFile : undefined;
    }
  }

  openFileSelection() {
    if(this.fileSelect) {
      this.fileSelect.nativeElement.click();
    }
  }

  chooseFile() {
    if(!this.file) {
      return;
    }
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const image = new Image();
      image.src = e.target.result;
      image.onload = rs => {
        const imgBase64Path: String = e.target.result;
        this.attachmentEvent.emit(imgBase64Path);
        console.log(imgBase64Path);
      };
    }
    reader.readAsDataURL(this.file);
  }
}
