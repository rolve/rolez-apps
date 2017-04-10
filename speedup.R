library(tidyverse)
library(scales)

data <- read_tsv("all_results.tsv") %>%
  mutate(
    impl  = factor(impl, levels = unique(impl)),
    tasks = factor(tasks)
  )

benchmarks = data$benchmark %>% unique %>% length
sizes = data %>%
  select(benchmark, n) %>%
  unique %>%
  mutate(size = factor(rep(c("small", "medium", "large"), benchmarks)))

data <- data %>% left_join(sizes) %>% select(-n)

mean_times <- data %>%
  group_by(benchmark, impl, size, tasks) %>%
  summarize(time = mean(time)) %>%
  ungroup

seq_java <- mean_times %>%
  filter(impl == "Java" & tasks == 1) %>%
  select(-impl, -tasks) %>%
  rename(base_time = time)

speedups_over_j <- mean_times %>%
  filter(impl != "Java") %>%
  left_join(seq_java) %>%
  mutate(speedup = base_time / time)

plot <- ggplot(speedups_over_j, aes(tasks, speedup, color = paste(benchmark, "  "))) +
  annotate("rect", xmin = -Inf, xmax = Inf, ymin = 0, ymax = 0.985, fill = "#00000011") +
  geom_line(aes(group = benchmark)) +
  geom_point() +
  facet_wrap(~size) +
  labs(title = "Speedup over sequential Java") +
  scale_x_discrete("Tasks") + 
  scale_y_continuous("Speedup", trans = "log2", breaks = c(0.5, 1, 2, 4, 8, 16)) +
  scale_color_discrete(NULL) +
  theme(
    plot.title = element_text(
      hjust = 0.5,
      size=16,
      face="bold", 
      margin = margin(5, 0, 10, 0)
    ),
    panel.grid.major.x = element_blank(),
    axis.ticks.x = element_blank(),
    legend.position = "bottom"
  ) +
  guides(color = guide_legend(nrow = 1))

ggsave(paste("speedup_over_java.pdf", sep = ""), plot, width = 9, height = 4)
