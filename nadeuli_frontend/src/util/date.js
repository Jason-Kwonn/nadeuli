import moment from 'moment';

export function formatMessageTimestamp(timestamp) {
  const messageTime = moment(timestamp);
  const currentTime = moment();
  const diffInHours = currentTime.diff(messageTime, 'hours');
  const diffInDays = currentTime.diff(messageTime, 'days');
  const diffInWeeks = currentTime.diff(messageTime, 'weeks');

  if (diffInHours < 24) {
    return messageTime.format("A h:mm");
  } else if (diffInDays < 2) {
    return '어제';
  } else if (diffInDays <= 7) {
    return `${diffInDays}일 전`;
  } else {
    return `${diffInWeeks}주 전`;
  }
}