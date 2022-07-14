import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'tagify'
})
export class TagifyPipe implements PipeTransform {

  transform(value: string | String, ...args: unknown[]): unknown {
    return this.stylize(value);
  }

  private stylize(text: string | String): string | String {
    let stylizedText: string = '';
    if (text && text.length > 0) {
      for (let line of text.split("\n")) {
        for (let t of line.split(" ")) {
          if (t.startsWith("#") && t.length>1) {  
            stylizedText += `#<a href="/tag/${t.substring(1)}">${t.substring(1)}</a> `;
          }
          else if (t.startsWith("@") && t.length>1) {  
            stylizedText += `@<a href="/user/${t.substring(1)}">${t.substring(1)}</a> `;
          }
          else
            stylizedText += t + " ";
        }
        stylizedText += '<br>';
      }
      return stylizedText;
    }
    else return text;
  }
}
