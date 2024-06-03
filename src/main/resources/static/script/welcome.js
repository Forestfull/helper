const bannerTitle = '<h1 style="text-align: center"> ' + location.pathname + '</h1>';
welcomeHtml = '<h3 id="welcome-description" style="width: 50vw; margin: 10px auto; font-size: 1rem;">' +
    '<p>Waiting for permission from server.</p>' +
    '<p>Please wait a moment</p>' +
    '<p id="first-loading-char"></p>' +
    '</h3>';

let clientHistory;

function typingHTML(option) {
    const className = option.className;
    const html = option.html;
    const callback = option.callback;

    if (html instanceof Array) {
        for (let i = 0; i < html.length; i++) {
            let element = html[i];
            if (i !== html.length - 1)
                element.callback = undefined;
            typingHTML(element);
        }
        return;
    }

    if (html.length === 0) {
        if (callback !== undefined)
            callback();
        return; // 더 이상 없으므로 종료
    }

    function getInnerTag(parent, innerHTML) {
        let tagNameProperties = innerHTML.substring(1, innerHTML.indexOf('>'));
        let whiteSpaceIndex = tagNameProperties.indexOf(' ');
        const tagName = whiteSpaceIndex !== -1 ? tagNameProperties.substring(0, whiteSpaceIndex) : tagNameProperties;

        let endTagSlashIndex = innerHTML.indexOf('</' + tagName);
        let otherStartTagIndex = innerHTML.substring(1, endTagSlashIndex).indexOf('<' + tagName);

        while (otherStartTagIndex !== -1) {
            endTagSlashIndex = innerHTML.indexOf('</' + tagName, endTagSlashIndex + 1);
            otherStartTagIndex = innerHTML.substring(otherStartTagIndex + tagName.length + 2, innerHTML.length).indexOf('<' + tagName);

            if (endTagSlashIndex === -1) // 종료태그가 없을 경우 던지기
                throw new DOMException('near by "' + innerHTML.substring(0, Math.min(50, innerHTML.length)) + '"', 'parsingError')
        }

        let tagContent = innerHTML.substring(innerHTML.indexOf('>') + 1, endTagSlashIndex);

        let tagElement;

        let tagRandomClass = 'typingTag-' + Math.floor(Math.random() * 10000);
        if (whiteSpaceIndex !== -1) {
            let tagSetting = tagNameProperties.substring(tagName.length, tagNameProperties.length);
            let classList = '';
            if (tagSetting.indexOf('class') !== -1) {
                classList = tagSetting.substring(tagSetting.indexOf('"', tagSetting.indexOf('class') + 1) + 1, tagSetting.indexOf('"', tagSetting.indexOf('class') + 7));
                tagSetting = tagSetting.replace(classList, '');
                tagSetting = tagSetting.replace('class=', '');
                tagSetting = tagSetting.replace('class =', '');

            }

            parent.innerHTML += '<' + tagName + ' ' + tagSetting + ' class="' + tagRandomClass + ' ' + classList + '">' + innerHTML.substring(endTagSlashIndex, innerHTML.indexOf('>', endTagSlashIndex) + 1);

            tagElement = document.querySelector('.' + tagRandomClass);

        } else {
            tagElement = document.createElement(tagName);
            tagElement.classList.add(tagRandomClass);
            parent.appendChild(tagElement);

        }

        if (tagContent.indexOf('</') !== -1) {
            let tagArrays = [];

            while (tagContent.trim() !== '') {
                let tagName = tagContent.substring(tagContent.indexOf('<') + 1, tagContent.indexOf('>'));
                if (tagName.indexOf(' ') !== -1) tagName = tagName.substring(0, tagName.indexOf(' '));

                const tagEndIndex = tagContent.indexOf('</' + tagName + '>') + ('</' + tagName + '>').length;

                let content = tagContent.substring(0, tagEndIndex);
                tagArrays.push(getInnerTag(tagElement, content));

                tagContent = tagContent.substring(tagEndIndex, tagContent.length);
            }

            return {className: tagElement.className, html: tagArrays, interval: interval, callback: callback};

        } else {
            return {className: tagElement.className, html: tagContent, interval: interval, callback: callback};

        }
    }

    const interval = option.interval === undefined ? 10 : option.interval;

    return setTimeout(() => {
        const htmlElement = document.querySelector('.' + className.trim().replaceAll(' ', '.'));
        let parsedCharacter = html.charAt(0);

        if (htmlElement === undefined || htmlElement === null) return;
        if (parsedCharacter === '') return;

        if (parsedCharacter === '<') { // 태그 검색
            let innerTag = getInnerTag(htmlElement, html);

            typingHTML({
                className: innerTag.className,
                html: innerTag.html,
                interval: interval,
                callback: callback
            });

        } else {
            if (parsedCharacter === '&') {
                htmlElement.innerHTML += html.substring(0, html.indexOf(';') + 1);

                typingHTML({
                    className: className,
                    html: html.substring(html.indexOf(';') + 1, html.length),
                    interval: interval,
                    callback: callback
                });
            } else {
                htmlElement.innerHTML += parsedCharacter;

                typingHTML({
                    className: className,
                    html: html.substring(1, html.length),
                    interval: interval,
                    callback: callback
                });
            }
        }
    }, interval);
}

typingHTML({
    className: 'waiting-header',
    html: bannerTitle,
    interval: 20
});

function loading() {
    const waitDot = document.getElementById('first-loading-char');
    waitDot.innerHTML = '&nbsp;';
    return setInterval(() => {
        const dotLength = waitDot.innerText.split('.').length;
        if (dotLength > 3) {
            waitDot.innerText = '.';
        } else {
            waitDot.innerText += '.';
        }
    }, 200);
}

function complete(waitDotAddr) {
    clearInterval(waitDotAddr);
    document.getElementById('first-loading-char').innerText = '[Success]';
}

typingHTML({
    className: 'waiting-explain',
    html: welcomeHtml,
    interval: 20,
    callback: () => {
        setTimeout(() => {
            const waitDotAddr = loading();
            fetch('/support/history?token=' + location.pathname.substring(1, location.pathname.length), {})
                .then(response => response.json())
                .then(body => {
                    complete(waitDotAddr);
                    clientHistory = body
                    for (let chat of clientHistory) {
                        const className = chat?.type === 'response' ? 'other-chat' : 'my-chat';

                        let author = chat?.type === 'response' ? 'Answer' : chat?.client?.code;
                        document.querySelector('.client-history').innerHTML
                            += '<section class="' + className + '">' +
                            '<article class="author">' + author + '</article>' +
                            '<article class="ip">' + chat?.ipAddress + '</article>' +
                            '<article class="time">' + chat?.createdTime + '</article>' +
                            '<article class="chat">' + chat?.data + '</article>' +
                            '</section>'
                    }

                    document.querySelector('.client-history').scrollTop = document.querySelector('.client-history').scrollHeight;
                })
                .catch(reason => {
                    clearInterval(waitDotAddr);
                });
        }, welcomeHtml.length * 2);
    }
});

document.getElementById('requestAfterService').addEventListener('click', e => {
    e.preventDefault();
    let addr = loading();

    fetch('/support', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': location.pathname.substring(1, location.pathname.length)
        },
        body: document.querySelector('.client-request > textarea').value
    })
        .then(response => {
            complete(addr);
            document.querySelector('.client-request > textarea').value = '';
            fetch('/support/history?token=' + location.pathname.substring(1, location.pathname.length) + '&exceptedIds=' + clientHistory.map(ch => ch.id))
                .then(response => response.json())
                .then(body => {
                    clientHistory.push(body);
                    for (let chat of body) {
                        const className = chat?.type === 'response' ? 'other-chat' : 'my-chat';

                        let author = chat?.type === 'response' ? 'Answer' : chat?.client?.code;
                        document.querySelector('.client-history').innerHTML
                            += '<section class="' + className + '">' +
                            '<article class="author">' + author + '</article>' +
                            '<article class="ip">' + chat?.ipAddress + '</article>' +
                            '<article class="time">' + chat?.createdTime + '</article>' +
                            '<article class="chat">' + chat?.data + '</article>' +
                            '</section>'
                    }
                    document.querySelector('.client-history').scrollTop = document.querySelector('.client-history').scrollHeight;
                    alert('completed your request.');
                });
        })
        .catch(reason => {
            alert('error has occurred. please try again');

            //기다려
        });
})