library(tidyverse)
library(scales)

data <- read_tsv("results.tsv")

implLevels <- (data %>% select(impl) %>% distinct)$impl
data <- mutate(data, impl = factor(impl, levels = implLevels))

plot <- ggplot(data, aes(factor(tasks), time, fill = impl)) +
  geom_bar(
    stat = "summary",
    width = 0.9,
    position = "dodge"
  ) +
  geom_errorbar(
    stat = "summary",
    width = 0.2,
    position = position_dodge(width = 0.9)
  ) +
  # geom_text(
  #   data = data %>% filter(impl == "Rolez") %>% group_by(impl, n, tasks) %>% summarise(time = mean(time)),
  #   aes(label = time),
  #   check_overlap = TRUE,
  #   vjust = "outward"
  # ) +
  facet_wrap(
    ~n,
    scales = "free_y",
    labeller = as_labeller(function(labels) {
      return(paste0("n = ", labels))
    })) +
  scale_x_discrete("Tasks") + 
  scale_y_continuous("Execution time [s]") +
  scale_fill_discrete(NULL) +
  theme(
    panel.grid.major.x = element_blank(),
    axis.ticks.x = element_blank(),
    legend.position = "bottom"
  )

ns <- data %>% select(n) %>% distinct() %>% count %>% unlist(use.names = FALSE)
ggsave("results.pdf", plot, width = 3 * ns, height = 4)
