library(tidyverse)
library(scales)

data <- read_tsv("results.tsv") %>%
  mutate(
    impl  = factor(impl, levels = unique(impl)),
    tasks = factor(tasks)
  )

benchmark <- data %>% select(benchmark) %>% unique() %>% .[[1]]

# Execution time bar plots
plot <- ggplot(data, aes(tasks, time, fill = impl)) +
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
  facet_wrap(
    ~n,
    scales = "free_y",
    labeller = as_labeller(function(labels) {
      return(paste0("n = ", labels))
    })) +
  labs(title = benchmark) +
  scale_x_discrete("Tasks") + 
  scale_y_continuous("Execution time [s]") +
  scale_fill_discrete(NULL) +
  theme(
    plot.title = element_text(
      hjust = 0.5,
      size=18,
      face="bold", 
      margin = margin(5, 0, 10, 0)
    ),
    panel.grid.major.x = element_blank(),
    axis.ticks.x = element_blank(),
    legend.position = "bottom"
  )

ns <- data %>% select(n) %>% distinct() %>% count %>% unlist(use.names = FALSE)
ggsave(paste(benchmark, "_execution_time.pdf", sep = ""), plot, width = 3 * ns, height = 4)


# Self-relative speedup plots
mean_times <- data %>%
  group_by(benchmark, impl, n, tasks) %>%
  summarize(time = mean(time))

base_times <- mean_times %>%
  filter(tasks == 1) %>%
  select(-tasks) %>%
  rename(base_time = time)

speedups <- mean_times %>%
  left_join(base_times, suffix = c("", "_")) %>%
  mutate(speedup = base_time / time)

plot <- ggplot(speedups, aes(tasks, speedup, color = impl)) +
  geom_point() +
  geom_line(aes(group = impl)) +
  facet_wrap(
    ~n,
    labeller = as_labeller(function(labels) {
      return(paste0("n = ", labels))
    })) +
  labs(title = benchmark) +
  scale_x_discrete("Tasks") + 
  scale_y_continuous("Speedup", breaks = pretty_breaks(n = 5)) +
  scale_color_discrete(NULL) +
  theme(
    plot.title = element_text(
      hjust = 0.5,
      size=18,
      face="bold", 
      margin = margin(5, 0, 10, 0)
    ),
    panel.grid.major.x = element_blank(),
    axis.ticks.x = element_blank(),
    legend.position = "bottom"
  )

ggsave(paste(benchmark, "_speedup.pdf", sep = ""), plot, width = 3 * ns, height = 4)

