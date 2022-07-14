import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'tagify'
})
export class TagifyPipe implements PipeTransform {

  transform(value: string | String, ...args: unknown[]): unknown {
    return this.stylize(value);
  }

  private stylize(text: string | String): string | String {
    let stylizedText: string | String = text;
    let tagPattern: RegExp = /(\s|^|>)#(\w+)/g;
    stylizedText = stylizedText.replace(tagPattern, "$1#<a href=\"/tag/$2\">$2</a>");
    let mentionPattern: RegExp = /(\s|^|>)@(\w+)/g;
    stylizedText = stylizedText.replace(mentionPattern, "$1@<a href=\"/user/$2\">$2</a>");
    return stylizedText;
  }
}
