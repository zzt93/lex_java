
enum {
    $1
};

//put method here
$2

// use last enum element to replace it
int count[$3 + 1];

int main() {
    char word[256];
    while (scanf("%s", word) >= 0) {
        int type = lexical(word);

        printf("(%d, %s, %d)", type, word, count[type]++);
    }
}

int lexical(char *str) {
    $4
}