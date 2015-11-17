
enum {
    #1
};

//put method here
#2

// use last enum element to replace it
int count[#3];

// the state number of DFA
int state = 0;
##define FOUND -1

int main() {
    char c;
    int i = 0;
    while (scanf("%c", c) >= 0) {
        word[i++] = c;
        int type = lexical(c);
        if (state == FOUND) {
            word[i] = '\0';
            printf("(%d, %s, %d)\n", type, word, count[type]++);
            state = 0;
            i = 0;
        }
    }
}

int lexical(char c) {
    #4
}