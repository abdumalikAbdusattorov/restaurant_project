package uz.pdp.appmilliytaomlarserver.bot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.appmilliytaomlarserver.entity.*;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderType;
import uz.pdp.appmilliytaomlarserver.entity.enums.PayStatus;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.repository.*;
import uz.pdp.appmilliytaomlarserver.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TelBotServer {

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;


    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RoomsRepository roomsRepository;

    @Autowired
    DeletingMessageRepository deletingMessageRepository;

    @Autowired
    DeletingMessageWithPhotoRepository deletingMessageWithPhotoRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;

    @Autowired
    MillyTaomBot millyTaomBot;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyInfoRepository companyInfoRepository;

    public void sendToClientWithPhoto(SendMessage sendMessage) {
        try {

            List<DeletingMessage> messageList = deletingMessageRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessage deletingMessage : messageList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                millyTaomBot.execute(deleteMessage);
                deletingMessageRepository.delete(deletingMessage);
            }
            List<DeletingMessageWithPhoto> messageWithPhotoList = deletingMessageWithPhotoRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessageWithPhoto deletingMessage : messageWithPhotoList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                millyTaomBot.execute(deleteMessage);
                deletingMessageWithPhotoRepository.delete(deletingMessage);
            }
            Message message = millyTaomBot.execute(sendMessage);
            getFutureDeletingMessageData(message.getMessageId(), message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void getFutureDeletingMessageData(Integer messageId, Long chatId) {
        deletingMessageRepository.save(new DeletingMessage(messageId, chatId));
    }

    public void getFutureDeletingMessageWithPhotoData(Integer messageId, Long chatId) {
        deletingMessageWithPhotoRepository.save(new DeletingMessageWithPhoto(messageId, chatId));
    }

    public void sendToClient(SendMessage sendMessage) {
        try {

            List<DeletingMessage> messageList = deletingMessageRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessage deletingMessage : messageList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                millyTaomBot.execute(deleteMessage);
                deletingMessageRepository.delete(deletingMessage);
            }
            Message message = millyTaomBot.execute(sendMessage);
            getFutureDeletingMessageData(message.getMessageId(), message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient2(SendLocation sendLocation) {
        try {

            List<DeletingMessage> messageList = deletingMessageRepository.findAllByChatId(Long.parseLong(sendLocation.getChatId()));
            for (DeletingMessage deletingMessage : messageList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                millyTaomBot.execute(deleteMessage);
                deletingMessageRepository.delete(deletingMessage);
            }
            Message message = millyTaomBot.execute(sendLocation);
            getFutureDeletingMessageData(message.getMessageId(), message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    public SendMessage chooseLang(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        //Optional<CompanyInfo> byId = companyInfoRepository.findById(update.getMessage().getFrom().getId());
        //  boolean t=true;
        // List<CompanyInfo> allByBotActive = companyInfoRepository.findAllByBotActive(t);
        // companyInfoRepository.findAllById();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBotState(BotState.CHOOSE_LANG);
            userRepository.save(user);
        } else {
            User user = new User();
            user.setTelegramChatId(update.getMessage().getFrom().getId());
            user.setBotState(BotState.CHOOSE_LANG);
            userRepository.save(user);
        }
        sendMessage.setText("Iltimos, til tanlang.\nПожалуста выберите язык");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("\uD83C\uDDFA\uD83C\uDDFF  O'zbek tili");
        keyboardRow1.add(keyboardButton);
        keyboardButton = new KeyboardButton();
        keyboardButton.setText("\uD83C\uDDF7\uD83C\uDDFA  Русский язык");
        keyboardRow1.add(keyboardButton);
        keyboard.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);


        return sendMessage;
    }

    public SendMessage getLang(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (text.contains("O'zbek tili")) {
                user.setLang("UZ");
            }
            if (text.contains("Русский язык")) {
                user.setLang("RU");
            }
            User saveUser = userRepository.save(user);
            if (saveUser.getPhoneNumber() != null) {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();

                List<CompanyInfo> all = companyInfoRepository.findAll();
                boolean deliveryActive = all.get(0).isDeliveryActive();
                if (deliveryActive) {
                    keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83C\uDFC3 Olib ketish" : "\uD83C\uDFC3 Самовывоз");
                    keyboardRow1.add(keyboardButton);
                    keyboardButton = new KeyboardButton();
                    keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83D\uDE98 Yetkazib berish" : "\uD83D\uDE98 Доставка");
                    keyboardRow1.add(keyboardButton);
                    keyboard.add(keyboardRow1);
                    keyboardRow1 = new KeyboardRow();
                    keyboardButton = new KeyboardButton();

                    keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83E\uDE91 Joy zakaz \uD83D\uDCDE" : "\uD83E\uDE91 предзаказ \uD83D\uDCDE");
                    keyboardRow1.add(keyboardButton);
                    keyboard.add(keyboardRow1);
                    keyboardRow1 = new KeyboardRow();
                    keyboardButton = new KeyboardButton();

                } else {
                    keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83C\uDFC3 Olib ketish" : "\uD83C\uDFC3 Самовывоз");
                    keyboardRow1.add(keyboardButton);
                    keyboard.add(keyboardRow1);
                    keyboardRow1 = new KeyboardRow();

                    keyboardButton = new KeyboardButton();
                    keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83E\uDE91 Joy zakaz \uD83D\uDCDE" : "\uD83E\uDE91 предзаказ \uD83D\uDCDE");
                    keyboardRow1.add(keyboardButton);
                    keyboard.add(keyboardRow1);
                    keyboardRow1 = new KeyboardRow();
                    keyboardButton = new KeyboardButton();

                }

                keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83C\uDDF7\uD83C\uDDFA Tilni o'zgartir o' \uD83C\uDDFA\uD83C\uDDFF" : " \uD83C\uDDFA\uD83C\uDDFF Изменить язык \uD83C\uDDFA\uD83C\uDDFF");
                keyboardRow1.add(keyboardButton);
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(saveUser.getLang().equals("UZ") ? "ℹ️ Biz haqimizda" : "ℹ️ Хотите много полезной информации?");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText(saveUser.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
                user.setBotState(BotState.SHARE_ORDER_TYPE);
                userRepository.save(user);
            } else {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton().setRequestContact(true);
                keyboardButton.setText(saveUser.getLang().equals("UZ") ? "Jo'natish" : "Отправить");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                user.setBotState(BotState.SHARE_CONTACT);
                userRepository.save(user);
                sendMessage.setText(saveUser.getLang().equals("UZ") ? "Telefon raqamingizni jo'nating" : "Отправте номер телефона");
            }
        }
        return sendMessage;
    }

    public SendMessage getbackToGender(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();

            List<CompanyInfo> all = companyInfoRepository.findAll();
            boolean deliveryActive = all.get(0).isDeliveryActive();
            if (deliveryActive) {
                keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83C\uDFC3 Olib ketish" : "\uD83C\uDFC3 Самовывоз");
                keyboardRow1.add(keyboardButton);
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83D\uDE98 Yetkazib berish" : "\uD83D\uDE98 Доставка");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();

                keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83E\uDE91 Joy zakaz \uD83D\uDCDE" : "\uD83E\uDE91 предзаказ \uD83D\uDCDE");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();

            } else {
                keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83C\uDFC3 Olib ketish" : "\uD83C\uDFC3 Самовывоз");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();

                keyboardButton = new KeyboardButton();
                keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83E\uDE91 Joy zakaz \uD83D\uDCDE" : "\uD83E\uDE91 предзаказ \uD83D\uDCDE");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();

            }

            keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83C\uDDF7\uD83C\uDDFA Tilni o'zgartir o' \uD83C\uDDFA\uD83C\uDDFF" : " \uD83C\uDDFA\uD83C\uDDFF Изменить язык \uD83C\uDDFA\uD83C\uDDFF");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(user.getLang().equals("UZ") ? "ℹ️ Biz haqimizda" : "ℹ️ Хотите много полезной информации?");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText(user.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
            user.setBotState(BotState.SHARE_ORDER_TYPE);
            userRepository.save(user);
        }
        return sendMessage;
    }

    public SendMessage getPhoneNumber(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getMessage().getChatId());
        String phoneNumber = getCheckPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPhoneNumber(phoneNumber);
            user.setBotState(BotState.SHARE_FIRST_NAME);
            User saveUser = userRepository.save(user);
            sendMessage.setText(saveUser.getLang().equals("UZ") ? "Ism kirting" : "Введите ваше имя");

        } else {

        }
        return sendMessage;
    }

    public String getCheckPhoneNumber(String phoneNumber) {
        if (phoneNumber.trim().contains("+")) {
            return phoneNumber;
        } else {
            return "+" + phoneNumber;
        }
    }

    public SendMessage getFirstName(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirst_name(update.getMessage().getText());
            User saveUser = userRepository.save(user);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            // //
            keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83C\uDFC3 Olib ketish" : "\uD83C\uDFC3 Самовывоз");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83D\uDE98 Yetkazib berish" : "\uD83D\uDE98 Доставка");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83E\uDE91 Joy zakaz \uD83D\uDCDE" : "\uD83E\uDE91 предзаказ \uD83D\uDCDE");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(saveUser.getLang().equals("UZ") ? "\uD83C\uDDF7\uD83C\uDDFA Tilni o'zgartir o' \uD83C\uDDFA\uD83C\uDDFF" : " \uD83C\uDDFA\uD83C\uDDFF Изменить язык \uD83C\uDDFA\uD83C\uDDFF");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(saveUser.getLang().equals("UZ") ? "ℹ️ Biz haqimizda" : "ℹ️ Хотите много полезной информации?");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText(saveUser.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
            user.setBotState(BotState.SHARE_ORDER_TYPE);
            userRepository.save(user);
        }

        return sendMessage;
    }

    public SendMessage getOrderType(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());

        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getFrom().getId() : update.getMessage().getFrom().getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (update.getCallbackQuery() != null) {

            } else {
                String text = update.getMessage().getText();
                if (text.contains("Olib ketish")) {
                    user.setTempOrderType(OrderType.OZIBORIBOLISH.name());
                }
                if (text.contains("Yetkazib berish")) {
                    user.setTempOrderType(OrderType.DASTAFKA.name());
                }
                if (text.contains("Joy zakaz")) {
                    user.setTempOrderType(OrderType.JOYZAKAZ.name());
                }
                user.setBotState(BotState.CHOOSE_CATEGORY);
            }

            User savedUser = userRepository.save(user);
            if (savedUser.getTempOrderType().equals("OZIBORIBOLISH")) {
                List<Category> allByParentIsNull = categoryRepository.findAllByParentIsNull();
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                int count = 0;
                for (Category category : allByParentIsNull) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(savedUser.getLang().equals("UZ") ? category.getTelegramIcon() + " " + category.getNameUz() : category.getTelegramIcon() + " " + category.getNameRu())
                            .setCallbackData("otaCategoryId:" + category.getId()));
                    if (count % 3 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 3 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(savedUser.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
            } else if (savedUser.getTempOrderType().equals("DASTAFKA")) {
                user.setBotState(BotState.CHOOSE_LOCATION_OR_ADDRESS);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton().setRequestLocation(true);
                keyboardButton.setText(savedUser.getLang().equals("UZ") ? "Lokatsiyani jo'natish" : "Отправить локатцию");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText(savedUser.getLang().equals("UZ") ? "Locatsiyzngizni jonating yoki addressingizni kiriting" : "Отправте локатция или введите адрес");
            } else {
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                int count = 0;
                for (int i = 0; i < 7; i++) {
                    Date date1 = new Date(date.getTime() + (i * 24 * 60 * 60 * 1000));
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(savedUser.getLang().equals("UZ") ? simpleDateFormat.format(date1) + "" : simpleDateFormat.format(date1) + "")
                            .setCallbackData("data:" + simpleDateFormat.format(date1)));
                    if (count % 3 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 3 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(savedUser.getLang().equals("UZ") ? "Chislo tanlang." : "Выберите к.");
                user.setBotState(BotState.CHOOSE_DATE);
            }
            userRepository.save(user);
        }
        return sendMessage;
    }

    public SendMessage getChildrenCatigoryOrProductId(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            int categoryId = Integer.parseInt(update.getCallbackQuery().getData().substring(14));
            List<Category> allByParentList = categoryRepository.findAllByParentId(categoryId);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            user.setTempCategoryId(categoryId);
            userRepository.save(user);
            if (allByParentList.size() > 0) {
                int count = 0;
                for (Category category : allByParentList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? category.getTelegramIcon() + " " + category.getNameUz() : category.getTelegramIcon() + " " + category.getNameRu())
                            .setCallbackData("chiCategoryId:" + category.getId()));
                    if (count % 3 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 3 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(user.getLang().equals("UZ") ? "Qani davom etamiz \uD83D\uDE09" : "Выберите категория.");
                user.setBotState(BotState.CHOOSE_CHILDERN_CATEGORY);
            } else {
                Page<Product> allProductByCategoryId = productRepository.findAllByCategoryId(categoryId, CommonUtils.getPageable(user.getPage(), user.getSize()));

                if (allProductByCategoryId.getContent().size() > 0) {
                    sendMessage.setText(user.getLang().equals("UZ") ? "Qani davom etamiz \uD83D\uDE09" : "Выберите категория.");
                    for (Product product : allProductByCategoryId.getContent()) {

                        if (!product.isActive()) {
                            sendMessage.setText(user.getLang().equals("UZ") ? "Hozir bunday mahsulot yoq \uD83D\uDE09" : "Выберите категория.");
                        } else {
                            rowInline.add(new InlineKeyboardButton()
                                    .setText(user.getLang().equals("UZ") ? product.getNameUz() : product.getNameRu())
                                    .setCallbackData("productId:" + product.getId()));
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                        }

                    }

                    if ((user.getPage() + 1) * user.getSize() <= allProductByCategoryId.getContent().size()) {
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "Keyingi mahsulotni korish" : "посмотреть следующий продукт")
                                .setCallbackData("nextPage"));
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                            .setCallbackData("backToGender"));
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                            .setCallbackData("back"));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    user.setBotState(BotState.CHOOSE_PRODUCT);
                } else {
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                            .setCallbackData("back"));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    user.setBotState(BotState.CHOOSE_CHILDERN_CATEGORY);
                    sendMessage.setText(user.getLang().equals("UZ") ? "Uzur bunaqa mahsulot yoq" : "Выберите категория.");
                }
            }
            userRepository.save(user);
        }

        return sendMessage;
    }

    public SendMessage getNextpageProduct(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setPage(user.getPage() + 1);


            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            Page<Product> allByCategoryId = productRepository.findAllByCategoryId(user.getTempCategoryId(), CommonUtils.getPageable(user.getPage(), user.getSize()));
            for (Product product : allByCategoryId) {
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? product.getNameUz() : product.getNameRu())
                        .setCallbackData("productId:" + product.getId()));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }

            if ((user.getPage() + 1) * user.getSize() <= allByCategoryId.getContent().size()) {
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Keyingi mahsulotni korish" : "посмотреть следующий продукт")
                        .setCallbackData("nextPage"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }

            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(user.getLang().equals("UZ") ? "Qani davom etamiz \uD83D\uDE09" : "Выберите категория.");
            user.setBotState(BotState.SHOUW_PRODUCT);
            userRepository.save(user);
        }
        return sendMessage;
    }

    public SendMessage getProductId(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();

            UUID productId = UUID.fromString(update.getCallbackQuery().getData().substring(10));
            user.setTempProductId(productId);
            User saveUser = userRepository.save(user);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            InlineKeyboardMarkup markupInlineFoto = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<List<InlineKeyboardButton>> rowsInlineFoto = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInlineFoto = new ArrayList<>();
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isPresent()) {

                Product product = optionalProduct.get();
                AttachmentContent file = attachmentContentRepository.getByAttachment(product.getAttachment());
                SendPhoto photo = new SendPhoto()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setParseMode(ParseMode.MARKDOWN)
                        .setPhoto("SetOfDishes" + product.getId(), new ByteArrayInputStream(file.getContent()));
                photo.setCaption(saveUser.getLang().equals("UZ") ? "*Nomi : " + product.getNameUz() + "*\nIzoh : " + product.getDescriptionUz() + "\nNarxi : " + CommonUtils.thousandSeparator((int) product.getPrice()) + " So'm" :
                        " Названия : " + product.getNameRu() + "\nОписания : " + product.getDescriptionRu() + "\nЦена : " + CommonUtils.thousandSeparator((int) product.getPrice()) + " Сум");


                rowInlineFoto.add(new InlineKeyboardButton()
                        .setText("Buyurtma berish")
                        .setCallbackData("ChoosenProductId:" + product.getId()));
                rowsInlineFoto.add(rowInlineFoto);
                markupInlineFoto.setKeyboard(rowsInlineFoto);
                photo.setReplyMarkup(markupInlineFoto);


                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(user.getLang().equals("UZ") ? "Mahsulot tanlang. " : "Выберите продукции.");
                Message execute;
                try {
                    execute = millyTaomBot.execute(photo);
                    getFutureDeletingMessageWithPhotoData(execute.getMessageId(), execute.getChatId());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            user.setBotState(BotState.CHOOSE_PRODUCT_FOTO);
            userRepository.save(user);
        }
        return sendMessage;
    }

    public SendMessage getProductSize(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            sendMessage.setText(user.getLang().equals("UZ") ? "Mahsulot sonini kirting " : "Введите количества продукции.");
            user.setBotState(BotState.CHOOSE_AMOUNT);
            userRepository.save(user);
        }
        return sendMessage;
    }

    public SendMessage getAmount(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        int amount = Integer.parseInt(update.getMessage().getText());

        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.SHOUW_TAME);
            User saveUser = userRepository.save(user);
            Optional<Orders> byOrderStatusAndUser = ordersRepository.findByOrderStatusAndClient(OrderStatus.DRAFT, saveUser);
            Product product = productRepository.findById(user.getTempProductId()).orElseThrow(() -> new ResourceNotFoundException("product", "id", user.getTempProductId()));
            //  String str = "";

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            if (byOrderStatusAndUser.isPresent()) {
                Orders orders = byOrderStatusAndUser.get();
                List<ProductWithAmount> productWithAmounts = productWithAmountRepository.findAllByOrders(orders);
                boolean bool = false;
                for (ProductWithAmount productWithAmount : productWithAmounts) {

                    if (productWithAmount.getProduct().getId().equals(product.getId())) {
                        bool = true;

                        productWithAmount.setAmount(productWithAmount.getAmount() + amount);
                        productWithAmountRepository.save(productWithAmount);
                    }

                }
                if (!bool) {
                    ProductWithAmount productWithAmount = new ProductWithAmount();
                    productWithAmount.setProduct(product);
                    productWithAmount.setAmount(amount);
                    productWithAmount.setOrders(orders);
                    productWithAmountRepository.save(productWithAmount);
                }
                List<ProductWithAmount> productWithAmounts2 = productWithAmountRepository.findAllByOrders(orders);
                //double totalSum = 0;
                for (int i = 0; i < productWithAmounts2.size(); i++) {

                    // totalSum += productWithAmounts2.get(i).getProduct().getPrice() * productWithAmounts2.get(i).getAmount();

                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? (i + 1) + ". " + productWithAmounts2.get(i).getProduct().getNameUz() + " " + productWithAmounts2.get(i).getAmount() + " x " + CommonUtils.thousandSeparator((int) productWithAmounts2.get(i).getProduct().getPrice()) + " ❌" + "\n"
                                    : (i + 1) + ". " + productWithAmounts2.get(i).getProduct().getNameRu() + " " + productWithAmounts2.get(i).getAmount() + " x " + CommonUtils.thousandSeparator((int) productWithAmounts2.get(i).getProduct().getPrice()) + " ❌" + "\n")
                            .setCallbackData("DeletingFromBasket:" + productWithAmounts2.get(i).getId()));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                // str += user.getLang().equals("UZ") ? "Jami: " + totalSum + " so'm" : "Итого: " + totalSum + " сум";
            } else {
                Orders orders = new Orders();
                orders.setClient(saveUser);
                orders.setOrderStatus(OrderStatus.DRAFT);
                Orders savedOrder = ordersRepository.save(orders);
                ProductWithAmount productWithAmount = new ProductWithAmount();
                productWithAmount.setAmount(amount);
                productWithAmount.setOrders(savedOrder);
                productWithAmount.setProduct(product);
                ProductWithAmount savedProduztW = productWithAmountRepository.save(productWithAmount);
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "1. " + product.getNameUz() + " " + amount + " x " + product.getPrice() + "\nJami: " + (amount * product.getPrice())
                                : "1. " + product.getNameRu() + " " + amount + " x " + product.getPrice() + "\nJami: " + (amount * product.getPrice()))
                        .setCallbackData("DeletingFromBasket:" + savedProduztW.getId()));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();

            }
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Yana maxsulot tanlash" : "Ище выберат продукции.")
                    .setCallbackData("continueOrder"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Xaridni yakunlash" : "Закончит заказ")
                    .setCallbackData("timeSave"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(user.getLang().equals("UZ") ? "Yana maxsulot tanlang yoki xaridni yakunlang" : "Выберите ище или закончите заказ");
        }

        return sendMessage;
    }

    public SendMessage removeBasketItem(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        UUID productWithAmountId = UUID.fromString(update.getCallbackQuery().getData().substring(19));
        productWithAmountRepository.deleteById(productWithAmountId);
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            Optional<Orders> byOrderStatusAndUser = ordersRepository.findByOrderStatusAndClient(OrderStatus.DRAFT, user);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (byOrderStatusAndUser.isPresent()) {
                Orders orders = byOrderStatusAndUser.get();
                List<ProductWithAmount> allByOrders = productWithAmountRepository.findAllByOrders(orders);
                if (allByOrders.size() > 0) {
                    for (int i = 0; i < allByOrders.size(); i++) {
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? (i + 1) + ". " + allByOrders.get(i).getProduct().getNameUz() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n"
                                        : (i + 1) + ". " + allByOrders.get(i).getProduct().getNameRu() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n")
                                .setCallbackData("DeletingFromBasket:" + allByOrders.get(i).getId()));
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton()
                            .setText(orders.getClient().getLang().equals("UZ") ? "Yana maxsulot tanlash" : "Ище выберат продукции.")
                            .setCallbackData("continueOrder"));
                    rowInline.add(new InlineKeyboardButton()
                            .setText(orders.getClient().getLang().equals("UZ") ? "Xaridni yakunlash" : "Закончит заказ")
                            .setCallbackData("finishOrder"));
                    rowsInline.add(rowInline);
                    sendMessage.setText(user.getLang().equals("UZ") ? "Yana mahsulot tanlang yoki buyurtmani yakunlang" : "Выберите ище или закончите заказ");

                } else {
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton()
                            .setText(orders.getClient().getLang().equals("UZ") ? "Maxsulot tanlash" : "Bыберат продукции.")
                            .setCallbackData("continueOrder"));
                    rowsInline.add(rowInline);
                    sendMessage.setText(user.getLang().equals("UZ") ? "Uzur hozircha mahsulot yoq" : "Извините сечас нечо нет");

                }
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
            }
        }
        return sendMessage;
    }

    public SendMessage getInformation(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        List<CompanyInfo> all = companyInfoRepository.findAll();
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();

            for (CompanyInfo companyInfo : all) {

                sendMessage.setText(user.getLang().equals("UZ") ? companyInfo.getName() + "\n" + companyInfo.getDescriptionUz() : companyInfo.getName() + "\n" + companyInfo.getDescriptionRu());
                userRepository.save(user);
            }
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83D\uDD19 Orqaga qaytish" : "\uD83D\uDD19 Назад");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83D\uDCCC Bizning manzil" : "\uD83D\uDCCC Филиалы");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

        }
        return sendMessage;
    }

    public SendLocation sendLocation(Update update) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(update.getMessage().getChatId());

        sendLocation.setLatitude(Float.parseFloat("41.316440"));
        sendLocation.setLongitude(Float.parseFloat("69.294860"));
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(user.getLang().equals("UZ") ? "\uD83D\uDD19 Orqaga qaytish \uD83D\uDD19" : "\uD83D\uDD19 Назад \uD83D\uDD19");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendLocation.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendLocation;
    }

    public SendMessage getFineshOrder(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            Optional<Orders> byOrderStatusAndUser = ordersRepository.findByOrderStatusAndClient(OrderStatus.DRAFT, user);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            String str = "";
            if (user.getTempOrderType().equals("OZIBORIBOLISH")) {
                if (byOrderStatusAndUser.isPresent()) {
                    Orders orders = byOrderStatusAndUser.get();
                    List<ProductWithAmount> allByOrders = productWithAmountRepository.findAllByOrders(orders);
                    double totalSum = 0;

                    if (allByOrders.size() > 0) {
                        for (int i = 0; i < allByOrders.size(); i++) {
                            rowInline.add(new InlineKeyboardButton()
                                    .setText(user.getLang().equals("UZ") ? (i + 1) + ". " + allByOrders.get(i).getProduct().getNameUz() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n"
                                            : (i + 1) + ". " + allByOrders.get(i).getProduct().getNameRu() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n")
                                    .setCallbackData("DeletingFromBasket:" + allByOrders.get(i).getId()));
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                            totalSum += allByOrders.get(i).getProduct().getPrice() * allByOrders.get(i).getAmount();

                        }
                        str += user.getLang().equals("UZ") ? "Jami: " + CommonUtils.thousandSeparator((int) totalSum) + " so'm" : "Итого: " + CommonUtils.thousandSeparator((int) totalSum) + " сум";
                        sendMessage.setText(str);
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB5 Naxt to'lash \uD83D\uDCB5" : "\uD83D\uDCB5 Заплатить наличными \uD83D\uDCB5")
                                .setCallbackData("naxtPul"));
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB3 PayMe \uD83D\uDCB3" : "\uD83D\uDCB3 PayMe \uD83D\uDCB3")
                                .setCallbackData("payme"));
                        rowsInline.add(rowInline);
                        markupInline.setKeyboard(rowsInline);
                        sendMessage.setReplyMarkup(markupInline);
                    } else {
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton()
                                .setText(orders.getClient().getLang().equals("UZ") ? "Maxsulot tanlash" : "Bыберат продукции.")
                                .setCallbackData("continueOrder"));
                        rowsInline.add(rowInline);
                        sendMessage.setText(user.getLang().equals("UZ") ? "Uzur hozircha mahsulot yoq" : "Извините сечас нечо нет");

                    }
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                }
            } else if (user.getTempOrderType().equals("DASTAFKA")) {
                if (byOrderStatusAndUser.isPresent()) {
                    Orders orders = byOrderStatusAndUser.get();
                    List<ProductWithAmount> allByOrders = productWithAmountRepository.findAllByOrders(orders);

                    List<CompanyInfo> all = companyInfoRepository.findAll();
                    int deliveryPricese = 0;
                    for (CompanyInfo companyInfoAll : all) {
                        deliveryPricese = companyInfoAll.getDeliveryPrice();

                    }
                    double totalSum = 0;
                    if (allByOrders.size() > 0) {
                        for (int i = 0; i < allByOrders.size(); i++) {
                            rowInline.add(new InlineKeyboardButton()
                                    .setText(user.getLang().equals("UZ") ? (i + 1) + ". " + allByOrders.get(i).getProduct().getNameUz() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n"
                                            : (i + 1) + ". " + allByOrders.get(i).getProduct().getNameRu() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n")
                                    .setCallbackData("DeletingFromBasket:" + allByOrders.get(i).getId()));
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                            totalSum += allByOrders.get(i).getProduct().getPrice() * allByOrders.get(i).getAmount();

                        }
                        str += user.getLang().equals("UZ") ? "Yetkazib berish" + deliveryPricese + "\n" + "Jami: " + CommonUtils.thousandSeparator((int) totalSum + deliveryPricese) + " so'm" :
                                "Даставка" + deliveryPricese + "\n" + "Итого: " + CommonUtils.thousandSeparator((int) totalSum) + " сум";
                        sendMessage.setText(str);
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB5 Naxt to'lash \uD83D\uDCB5" : "\uD83D\uDCB5 Заплатить наличными \uD83D\uDCB5")
                                .setCallbackData("naxtPul"));
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB3 PayMe \uD83D\uDCB3" : "\uD83D\uDCB3 PayMe \uD83D\uDCB3")
                                .setCallbackData("payme"));
                        rowsInline.add(rowInline);
                        markupInline.setKeyboard(rowsInline);
                        sendMessage.setReplyMarkup(markupInline);
                    } else {
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton()
                                .setText(orders.getClient().getLang().equals("UZ") ? "Maxsulot tanlash" : "Bыберат продукции.")
                                .setCallbackData("continueOrder"));
                        rowsInline.add(rowInline);
                        sendMessage.setText(user.getLang().equals("UZ") ? "Uzur hozircha mahsulot yoq" : "Извините сечас нечо нет");

                    }
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                }

            } else {
                if (byOrderStatusAndUser.isPresent()) {

                    Orders orders = byOrderStatusAndUser.get();
                    List<ProductWithAmount> allByOrders = productWithAmountRepository.findAllByOrders(orders);
                    List<Orders> all = ordersRepository.findAll();

                    double totalSum = 0;

                    if (allByOrders.size() > 0) {
                        for (int i = 0; i < allByOrders.size(); i++) {
                            rowInline.add(new InlineKeyboardButton()
                                    .setText(user.getLang().equals("UZ") ? (i + 1) + ". " + allByOrders.get(i).getProduct().getNameUz() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n"
                                            : (i + 1) + ". " + allByOrders.get(i).getProduct().getNameRu() + " " + allByOrders.get(i).getAmount() + " x " + allByOrders.get(i).getProduct().getPrice() + " ❌" + "\n")
                                    .setCallbackData("DeletingFromBasket:" + allByOrders.get(i).getId()));
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                            double romSum = orders.getRooms().getPrice();
                            totalSum += allByOrders.get(i).getProduct().getPrice() * allByOrders.get(i).getAmount() + romSum;

                        }
                        str += user.getLang().equals("UZ") ? "Hona narxi: " + CommonUtils.thousandSeparator((int) orders.getRooms().getPrice()) + " So'm\n" + "Jami: " + CommonUtils.thousandSeparator((int) totalSum) + " so'm" : "цена дома: " + CommonUtils.thousandSeparator((int) orders.getRooms().getPrice()) + " Сум\n" + "Итого: " + CommonUtils.thousandSeparator((int) totalSum) + " сум";
                        sendMessage.setText(str);
                    } else {
                        for (Orders orderss : all) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            sendMessage.setText(user.getLang().equals("UZ") ? "Siz *" + simpleDateFormat.format(orderss.getOrderDateTime()) + "* chisloda " + orders.getRooms().getDescriptionUz() + " honani band qildingiz \n" + "Hona narxi*" + CommonUtils.thousandSeparator((int) orderss.getRooms().getPrice()) + "* So'm" : "Извините сечас нечо нет");
                        }
                    }
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB5 Naxt to'lash \uD83D\uDCB5" : "\uD83D\uDCB5 Заплатить наличными \uD83D\uDCB5")
                            .setCallbackData("naxtPul"));
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "\uD83D\uDCB3 PayMe \uD83D\uDCB3" : "\uD83D\uDCB3 PayMe \uD83D\uDCB3")
                            .setCallbackData("payme"));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);

                }
            }
        }
        return sendMessage;
    }

    public SendMessage getLocation(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            if (update.getMessage().getLocation() != null) {
                Location location = update.getMessage().getLocation();
                user.setLan((double) location.getLongitude());
                user.setLat((double) location.getLatitude());
            } else {
                user.setAddress(update.getMessage().getText());
            }
            User savedUser = userRepository.save(user);
            List<Category> allByParentIsNull = categoryRepository.findAllByParentIsNull();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            int count = 0;
            for (Category category : allByParentIsNull) {
                count++;
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? category.getTelegramIcon() + " " + category.getNameUz() : category.getTelegramIcon() + " " + category.getNameRu())
                        .setCallbackData("otaCategoryId:" + category.getId()));
                if (count % 3 == 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
            }
            if (count % 3 != 0) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(savedUser.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
        }
        return sendMessage;
    }

    public SendMessage getDate(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        String date = update.getCallbackQuery().getData().substring(5);
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            Date date1;
            try {
                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                user.setTempChusenDate(date1);
                User savedUser = userRepository.save(user);
                List<Rooms> all = roomsRepository.findAll();
                Timestamp ordetDate = new Timestamp(date1.getTime());

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<Rooms> freeRoomList = new ArrayList<>();
                int count = 0;
                for (Rooms rooms : all) {
                    Optional<Orders> byOrderDateTimeAndRooms = ordersRepository.findByOrderDateTimeAndRooms(ordetDate, rooms);

                    if (!byOrderDateTimeAndRooms.isPresent()) {
                        freeRoomList.add(rooms);
                    }
                }
                if (freeRoomList.size() > 0) {
                    for (Rooms rooms : freeRoomList) {
                        count++;
                        rowInline.add(new InlineKeyboardButton()
                                .setText(savedUser.getLang().equals("UZ") ? rooms.getDescriptionUz() : rooms.getDescriptionRu())
                                .setCallbackData("roomId:" + rooms.getId()));
                        if (count % 3 == 0) {
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                        }
                    }
                    sendMessage.setText(savedUser.getLang().equals("UZ") ? "Hona tanlang" : "Выберите категория.");
                } else {
                    sendMessage.setText(savedUser.getLang().equals("UZ") ? "Bu sanada honalar band\nBoshqa xona tanlang" : "Bu sanada honalar band\nBoshqa xona tanlang.");
                }
                if (count % 3 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? "Boshqa sana tanlash" : "Выберите другую комнату")
                        .setCallbackData("backNewData"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return sendMessage;
    }

    public SendMessage getNewDate(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.CHOOSE_DATE);
            User savedUser = userRepository.save(user);

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            int count = 0;
            for (int i = 0; i < 7; i++) {
                Date date1 = new Date(date.getTime() + (i * 24 * 60 * 60 * 1000));
                count++;
                rowInline.add(new InlineKeyboardButton()
                        .setText(savedUser.getLang().equals("UZ") ? simpleDateFormat.format(date1) + "" : simpleDateFormat.format(date1) + "")
                        .setCallbackData("data:" + simpleDateFormat.format(date1)));
                if (count % 3 == 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
            }
            if (count % 3 != 0) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(savedUser.getLang().equals("UZ") ? "Chislo tanlang." : "Выберите к.");

        }
        return sendMessage;
    }

    public SendMessage getRoomId(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        String roomId = update.getCallbackQuery().getData().substring(7);
        Rooms rooms = roomsRepository.findById(Integer.parseInt(roomId)).orElseThrow(() -> new ResourceNotFoundException("id", "room", roomId));
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.CHOOSE_DATE);
            User savedUser = userRepository.save(user);
            Optional<Orders> optionalOrders = ordersRepository.findByOrderStatusAndClient(OrderStatus.DRAFT, user);
            Date date = user.getTempChusenDate();
            Timestamp ts = new Timestamp(date.getTime());
            if (optionalOrders.isPresent()) {
                Orders orders = optionalOrders.get();
                orders.setOrderDateTime(ts);
                orders.setRooms(rooms);
                ordersRepository.save(orders);
            } else {
                Orders orders = new Orders();
                orders.setRooms(rooms);
                orders.setOrderDateTime(ts);
                orders.setOrderStatus(OrderStatus.DRAFT);
                orders.setClient(user);
                ordersRepository.save(orders);
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Hoz b/b" : "Вы з/е сейчас")
                    .setCallbackData("menu"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(savedUser.getLang().equals("UZ") ? "Shu kuni b/b" : "Вы з/е это сегодня?")
                    .setCallbackData("finishOrder"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(savedUser.getLang().equals("UZ") ? "Hozir buyurtma berasizmi yoki\nShu kuni buyurtma berasizmi" : "Вы заказываете сейчас или\nВы закажете это сегодня?");

        }
        return sendMessage;
    }

    public SendMessage getMenu(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();

            List<Category> allByParentIsNull = categoryRepository.findAllByParentIsNull();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            int count = 0;
            for (Category category : allByParentIsNull) {
                count++;
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? category.getTelegramIcon() + " " + category.getNameUz() : category.getTelegramIcon() + " " + category.getNameRu())
                        .setCallbackData("otaCategoryId:" + category.getId()));
                if (count % 3 == 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
            }
            if (count % 3 != 0) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(user.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
        }

        return sendMessage;
    }

    public SendMessage back(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove()); // tegidegi buttonni ochiradi
        sendMessage.setChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            if (user.getBotState().equals(BotState.CHOOSE_PRODUCT_FOTO)) {
                Page<Product> allProductByCategoryId = productRepository.findAllByCategoryId(user.getTempCategoryId(), CommonUtils.getPageable(user.getPage(), user.getSize()));

                if (allProductByCategoryId.getContent().size() > 0) {
                    sendMessage.setText(user.getLang().equals("UZ") ? "Qani davom etamiz \uD83D\uDE09" : "Выберите категория.");
                    for (Product product : allProductByCategoryId.getContent()) {

                        if (!product.isActive()) {
                            sendMessage.setText(user.getLang().equals("UZ") ? "Hozir bunday mahsulot yoq \uD83D\uDE09" : "Выберите категория.");
                        } else {
                            rowInline.add(new InlineKeyboardButton()
                                    .setText(user.getLang().equals("UZ") ? product.getNameUz() : product.getNameRu())
                                    .setCallbackData("productId:" + product.getId()));
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                        }
                    }

                    if ((user.getPage() + 1) * user.getSize() <= allProductByCategoryId.getContent().size()) {
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? "Keyingi mahsulotni korish" : "посмотреть следующий продукт")
                                .setCallbackData("nextPage"));
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                            .setCallbackData("backToGender"));
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                            .setCallbackData("back"));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    user.setBotState(BotState.CHOOSE_PRODUCT);
                    userRepository.save(user);
                }
            } else if (user.getBotState().equals(BotState.CHOOSE_CATEGORY)) {
                List<Category> allByParentList = categoryRepository.findAllByParentId(user.getTempCategoryId());
                if (allByParentList.size() > 0) {
                    int count = 0;
                    for (Category category : allByParentList) {
                        count++;
                        rowInline.add(new InlineKeyboardButton()
                                .setText(user.getLang().equals("UZ") ? category.getTelegramIcon() + " " + category.getNameUz() : category.getTelegramIcon() + " " + category.getNameRu())
                                .setCallbackData("chiCategoryId:" + category.getId()));
                        if (count % 3 == 0) {
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                        }
                    }
                    if (count % 3 != 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                            .setCallbackData("backToGender"));
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                            .setCallbackData("back"));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    sendMessage.setReplyMarkup(markupInline);
                    sendMessage.setText(user.getLang().equals("UZ") ? "Qani davom etamiz \uD83D\uDE09" : "Выберите категория.");
                    user.setBotState(BotState.CHOOSE_CHILDERN_CATEGORY);
                }
            }
        }

        return sendMessage;
    }

    public SendMessage getNaxt(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            Orders orders = new Orders();
            orders.setOrderStatus(OrderStatus.NEW);
            orders.setPayStatus(PayStatus.UNPAID);
            ordersRepository.save(orders);
            sendMessage.setText(user.getLang().equals("UZ") ? "Harid uchun raxmat" : "Спасибо за покупку");

        }
        return sendMessage;
    }

    public SendMessage getPayme(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            Orders orders = new Orders();
            orders.setOrderStatus(OrderStatus.NEW);
            orders.setPayStatus(PayStatus.UNPAID);
            ordersRepository.save(orders);
            sendMessage.setText(user.getLang().equals("UZ") ? "* 8600 2015 5421 2000 *\nBizning hisob raqam" + "\nHarid uchun raxmat" : "Спасибо за покупку");

        }
        return sendMessage;
    }

    public SendMessage getTame(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());
        ///////
        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.CHOOSE_TAME);

            userRepository.save(user);

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            int count = 0;
            for (int i = 6; i <= 21; i++) {
                count++;
                if (i > 9) {
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? i + ":00" : "06:00")
                            .setCallbackData("times:" + i + ":00"));
                } else {
                    rowInline.add(new InlineKeyboardButton()
                            .setText(user.getLang().equals("UZ") ? "0" + i + ":00" : "06:00")
                            .setCallbackData("times:" + "0" + i + ":00"));
                }


                if (count % 4 == 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
            }
            if (count % 4 != 0) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }

            if (user.getTempOrderType().equals("JOYZAKAZ")) {
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Shu kuni aytaman" : "Скажу в тот день")
                        .setCallbackData("ozimAytaman"));
            } else if (user.getTempOrderType().equals("DASTAFKA")) {
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Iloji boricha tezroq" : "Как можно быстрее")
                        .setCallbackData("tezroq"));
            } else {
                sendMessage.setText(user.getLang().equals("UZ") ? "Soat tanlang " : "Выбери часы  ");
            }
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);

            if (user.getTempChusenDate() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                sendMessage.setText(user.getLang().equals("UZ") ? simpleDateFormat.format(user.getTempChusenDate()) + " sanasida soat nechiga tayyor bo'lsin" : simpleDateFormat.format(user.getTempChusenDate()) + " Дата будьте готовы к какому времени");
            } else {
                sendMessage.setText(user.getLang().equals("UZ") ? "Soat tanlang " : "Выбери часы  ");

            }

        }

        return sendMessage;
    }

    public SendMessage getTimeSev(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        String time = update.getCallbackQuery().getData().substring(6);
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.SAVE_TAME);
            Date d;
            try {
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat dateFormat = new SimpleDateFormat("hh:mm");
                d = dateFormat.parse(time);
                user.setTempChusenTime(d);
                userRepository.save(user);
                if (user.getTempChusenDate() != null) {
                    sendMessage.setText(user.getLang().equals("UZ") ? simpleDateFormatDate.format(user.getTempChusenDate()) + " kuni\n" + simpleDateFormat.format(user.getTempChusenTime()) + " soatda mahsulotingiz tayyor bo'ladi." : simpleDateFormatDate.format(user.getTempChusenDate()) + " дата\n" + simpleDateFormat.format(user.getTempChusenTime()) + " ваш заказ будет готов через.");
                } else {
                    sendMessage.setText(user.getLang().equals("UZ") ? simpleDateFormat.format(user.getTempChusenTime()) + " soatda mahsulotingiz tayyor bo'ladi." : simpleDateFormat.format(user.getTempChusenTime()) + " ваш заказ будет готов через.");
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(user.getLang().equals("UZ") ? "Hardni tugatish" : "Скажу в тот день")
                        .setCallbackData("finishOrder"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sendMessage;
    }

    public SendMessage getOzimboribAndTezroq(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        String tezroqVaOzim = update.getCallbackQuery().getMessage().getText();
        Optional<User> byTelegramChatId = userRepository.findByTelegramChatId(update.getCallbackQuery().getFrom().getId());

        if (byTelegramChatId.isPresent()) {
            User user = byTelegramChatId.get();
            user.setBotState(BotState.SHOW_BASKET);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if(tezroqVaOzim.equals("tezroq")){
                user.setTezroq(tezroqVaOzim); //Dastafka
            }else {
                user.setOzimAytaman(tezroqVaOzim);//Joyzakaz
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(user.getLang().equals("UZ") ? "Hardni tugatish" : "Скажу в тот день")
                    .setCallbackData("finishOrder"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            userRepository.save(user);
            sendMessage.setText(user.getLang().equals("UZ") ? "Harid uchun tashakur":"Спасибо за покупку");

        }
        return sendMessage;
    }
}
