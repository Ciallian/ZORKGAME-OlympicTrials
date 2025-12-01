interface GameIO {
    void print(String s);
    void promptChoices(java.util.List<String> options);
    void setChoiceListener(GameIO.ChoiceListener listener);
    void clearChoices();


    interface ChoiceListener {
        void choiceSelected(int choice);
    }

}