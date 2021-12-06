package uz.pdp.appmilliytaomlarserver.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.appmilliytaomlarserver.entity.CompanyInfo;
import uz.pdp.appmilliytaomlarserver.entity.DeletingMessageWithPhoto;
import uz.pdp.appmilliytaomlarserver.entity.Orders;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.repository.CompanyInfoRepository;
import uz.pdp.appmilliytaomlarserver.repository.DeletingMessageWithPhotoRepository;
import uz.pdp.appmilliytaomlarserver.repository.OrdersRepository;
import uz.pdp.appmilliytaomlarserver.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
public class MillyTaomBot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    TelBotServer telBotServer;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyInfoRepository companyInfoRepository;

    @Autowired
    DeletingMessageWithPhotoRepository  deletingMessageWithPhotoRepository;

    @Override
    public void onUpdateReceived(Update update) {
        if (getBotActiveStatus()){
            if (update.hasMessage()) {
                if (update.getMessage().hasContact()) {
                    telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                    telBotServer.sendToClient(telBotServer.getPhoneNumber(update));
                }
                else if (update.getMessage().hasLocation()){
//                    telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                    telBotServer.sendToClient(telBotServer.getLocation(update));
                }
                else if (update.getMessage().hasText()) {
                    if (update.getMessage().getText().equals("/start")) {
                        telBotServer.sendToClient(telBotServer.chooseLang(update));
                    } else if (update.getMessage().getText().contains("O'zbek tili") || update.getMessage().getText().contains("Русский язык")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.getLang(update));
                    } else if (update.getMessage().getText().contains("Tilni o'zgartirish") || update.getMessage().getText().contains("Изменить язык")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.chooseLang(update));
                    } else if (update.getMessage().getText().contains("Biz haqimizda") || update.getMessage().getText().contains("Хотите много полезной информации?")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.getInformation(update));
                    } else if (update.getMessage().getText().equals("\uD83D\uDD19 Orqaga qaytish") || update.getMessage().getText().equals("\uD83D\uDD19 Назад")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.getLang(update));
                    } else if (update.getMessage().getText().equals("\uD83D\uDD19 Orqaga qaytish \uD83D\uDD19") || update.getMessage().getText().equals("\uD83D\uDD19 Назад \uD83D\uDD19")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.getInformation(update));
                    } else if (update.getMessage().getText().contains("Tilni o'zgartirish") || update.getMessage().getText().contains("Изменить язык")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient(telBotServer.chooseLang(update));
                    } else if (update.getMessage().getText().contains("\uD83D\uDCCC Bizning manzil") || update.getMessage().getText().contains("\uD83D\uDCCC Филиалы")) {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        telBotServer.sendToClient2(telBotServer.sendLocation(update));


                    } else {
                        telBotServer.getFutureDeletingMessageData(update.getMessage().getMessageId(), update.getMessage().getChatId());
                        Optional<User> optionalUser = userRepository.findByTelegramChatId(update.getMessage().getFrom().getId());
                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            if (user.getBotState().equals(BotState.SHARE_FIRST_NAME)) {
                                telBotServer.sendToClient(telBotServer.getFirstName(update));
                            }
                            if (user.getBotState().equals(BotState.SHARE_ORDER_TYPE)) {
                                telBotServer.sendToClient(telBotServer.getOrderType(update));
                            }if (user.getBotState().equals(BotState.CHOOSE_LOCATION_OR_ADDRESS)) {
                                telBotServer.sendToClient(telBotServer.getLocation(update));
                            }
                            if (user.getBotState().equals(BotState.CHOOSE_AMOUNT)) {
                                List<DeletingMessageWithPhoto> messageWithPhotoList = deletingMessageWithPhotoRepository.findAllByChatId(update.getMessage().getChatId());
                                for (DeletingMessageWithPhoto deletingMessage : messageWithPhotoList) {
                                    DeleteMessage deleteMessage = new DeleteMessage();
                                    deleteMessage.setChatId(deletingMessage.getChatId());
                                    deleteMessage.setMessageId(deletingMessage.getMessageId());
                                    try {
                                        execute(deleteMessage);
                                        deletingMessageWithPhotoRepository.delete(deletingMessage);
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }
                                telBotServer.sendToClient(telBotServer.getAmount(update));
                            }
                        }
                    }
                } else if (update.getMessage().hasLocation()) {

                }
            }
            else if (update.getCallbackQuery() != null) {
                if (update.getCallbackQuery().getData().startsWith("otaCategoryId:") || update.getCallbackQuery().getData().startsWith("chiCategoryId:")) {
                    telBotServer.sendToClientWithPhoto(telBotServer.getChildrenCatigoryOrProductId(update));
                }
                if (update.getCallbackQuery().getData().startsWith("productId:")) {
                    telBotServer.sendToClient(telBotServer.getProductId(update));
                }
                if (update.getCallbackQuery().getData().startsWith("data:")) {
                    telBotServer.sendToClient(telBotServer.getDate(update));
                }
                if (update.getCallbackQuery().getData().startsWith("DeletingFromBasket:")) {
                    telBotServer.sendToClient(telBotServer.removeBasketItem(update));
                }
                if (update.getCallbackQuery().getData().startsWith("roomId:")) {
                    telBotServer.sendToClient(telBotServer.getRoomId(update));
                }
                if (update.getCallbackQuery().getData().equals("ozimAytaman")||update.getCallbackQuery().getData().equals("tezroq")){
                    telBotServer.sendToClient(telBotServer.getOzimboribAndTezroq(update));
                }
                if (update.getCallbackQuery().getData().startsWith("timeSave")) {
                    telBotServer.sendToClient(telBotServer.getTame(update));
                }
                if (update.getCallbackQuery().getData().startsWith("times:")) {
                    telBotServer.sendToClient(telBotServer.getTimeSev(update));
                }
                if (update.getCallbackQuery().getData().startsWith("ChoosenProductId:")) {
                    telBotServer.sendToClient(telBotServer.getProductSize(update));
                }
                if (update.getCallbackQuery().getData().startsWith("finishOrder")) {
                    telBotServer.sendToClient(telBotServer.getFineshOrder(update));
                }
                if (update.getCallbackQuery().getData().equals("backNewData")) {
                    telBotServer.sendToClient(telBotServer.getNewDate(update));
                }
                if (update.getCallbackQuery().getData().equals("menu")) {
                    telBotServer.sendToClient(telBotServer.getMenu(update));
                }if (update.getCallbackQuery().getData().equals("backToGender")) {
                    telBotServer.sendToClient(telBotServer.getbackToGender(update));
                }
                if (update.getCallbackQuery().getData().equals("nextPage")) {
                    telBotServer.sendToClientWithPhoto(telBotServer.getNextpageProduct(update));
                }if (update.getCallbackQuery().getData().equals("continueOrder")) {
                    telBotServer.sendToClient(telBotServer.getMenu(update));
                }if (update.getCallbackQuery().getData().equals("naxtPul")) {
                    telBotServer.sendToClient(telBotServer.getNaxt(update));
                }if (update.getCallbackQuery().getData().equals("payme")) {
                    telBotServer.sendToClient(telBotServer.getPayme(update));
                }if (update.getCallbackQuery().getData().equals("back")) {
                    List<DeletingMessageWithPhoto> messageWithPhotoList = deletingMessageWithPhotoRepository.findAllByChatId(update.getCallbackQuery().getMessage().getChatId());
                    for (DeletingMessageWithPhoto deletingMessage : messageWithPhotoList) {
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(deletingMessage.getChatId());
                        deleteMessage.setMessageId(deletingMessage.getMessageId());
                        try {
                            execute(deleteMessage);
                            deletingMessageWithPhotoRepository.delete(deletingMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    telBotServer.sendToClient(telBotServer.back(update));
                }
            }
        }else {
            SendMessage sendMessage=new SendMessage();
            sendMessage.setChatId(update.getCallbackQuery() != null?update.getCallbackQuery().getMessage().getChatId():update.getMessage().getChatId());
            sendMessage.setText("Uzur bot sinov rejimida\n"+"Извините, бот в тестовом режиме");
            try {
                Message message = execute(sendMessage);
                telBotServer.getFutureDeletingMessageData(message.getMessageId(),message.getChatId());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public boolean getBotActiveStatus() {
        List<CompanyInfo> all = companyInfoRepository.findAll();
    return all.get(0).isBotActive();
    }
}
